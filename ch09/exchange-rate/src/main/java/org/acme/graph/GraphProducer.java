package org.acme.graph;

import com.fasterxml.jackson.core.JsonProcessingException;

import dev.langchain4j.agent.tool.ToolExecutionRequest;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageType;
import dev.langchain4j.data.message.SystemMessage;

import dev.langchain4j.data.message.UserMessage;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.acme.ai.CurrencyAgent;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.checkpoint.BaseCheckpointSaver;
import org.bsc.langgraph4j.checkpoint.MemorySaver;
import org.bsc.langgraph4j.langchain4j.serializer.std.ChatMesssageSerializer;
import org.bsc.langgraph4j.langchain4j.serializer.std.ToolExecutionRequestSerializer;
import org.bsc.langgraph4j.prebuilt.MessagesState;
import org.bsc.langgraph4j.serializer.std.ObjectStreamStateSerializer;
import org.bsc.langgraph4j.utils.EdgeMappings;
import org.jboss.logging.Logger;

import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@ApplicationScoped
public class GraphProducer {

    String systemMessageTemplate = """
            You are a specialized assistant for currency conversions.
            Your sole purpose is to use a tool to answer questions about currency exchange rates. 
            If the user asks about anything other than currency conversion or exchange rates, 
            politely state that you cannot help with that topic and can only assist with currency-related queries. 
            Do not attempt to answer unrelated questions or use tools for other purposes.
            
            NEVER use the same currency in from and to.
            
            Set response status to input_required if the user needs to provide more information.
            Set response status to error if there is an error while processing the request with information of the error.
            Set response status to completed if the request is complete.
    """;

    String userMessageTemplate = """
        You provide the conversion for the given currencies on the given date.
        If no date is provided then use the 'latest' string.
        
        User needs to provide both from and to currencies, if not, don't speculate putting both from and to currencies with the same value, 
        and return input_required. 
        Use latest only for the date.
        
        The conversation with information about the conversion is: {{conversion}}
    """;

    public static class State extends MessagesState<ChatMessage> {

        public State(Map<String, Object> initData) {
            super(initData);
        }

        public String question() {
            Optional<String> result = value("question");
            return result.orElseThrow( () -> new IllegalStateException( "question is not set!" ) );
        }

        public Optional<String> missingParameter() {
            final Optional<String> missingParameter = value("missingParameter");

            if (missingParameter.isPresent()
                && missingParameter.get().isEmpty()) {
                return Optional.empty();
            }

            return missingParameter;
        }

        public String result() {
            return (String) value("result").orElse("");
        }

    }

    @Produces
    @Singleton
    BaseCheckpointSaver memorySaver() {
        return new MemorySaver();
    }

    @Inject
    Logger logger;

    @Inject
    CurrencyAgent currencyAgent;

    private Map<String, Object> convert(State state) throws JsonProcessingException {
        logger.info("Converting Currencies calling model");

        final Prompt prompt = PromptTemplate.from(userMessageTemplate)
            .apply(Map.of("conversion", state.question()));

        final SystemMessage systemMessage = SystemMessage.from(systemMessageTemplate);
        final UserMessage userMessage = prompt.toUserMessage();

        final AiMessage aiMessage = currencyAgent.exchangeRate(systemMessage,
            userMessage);

        return Map.of("messages", List.of(systemMessage, userMessage, aiMessage));
    }

    private Map<String, Object> human_invoke(State state) {
        logger.info("Human Interaction");
        return Map.of();
    }

    private String isHumanInteractionRequired(State state) {
        final Optional<AiMessage> aiMessage = state.lastMessage()
            .filter(m -> ChatMessageType.AI == m.type())
            .map(m -> (AiMessage) m)
            .filter(AiMessage::hasToolExecutionRequests);

        return aiMessage.isPresent() ? "tool" : "human";
    }

    private Map<String, Object> invokeTool(State state) throws JsonProcessingException {

        logger.info("Converting Currencies invoking tool");

        final Optional<ToolExecutionRequest> toolExecutionRequest = state.lastMessage()
            .filter(m -> ChatMessageType.AI == m.type())
            .map(m -> (AiMessage) m)
            .filter(AiMessage::hasToolExecutionRequests)
            .map(ai -> ai.toolExecutionRequests().getFirst());

        final AiMessage aiMessage = currencyAgent
            .invokeTool(state.messages(), toolExecutionRequest.get());

        System.out.println(aiMessage.text());

        return Map.of("messages", aiMessage,
            "result", aiMessage.text(),
            "missingParameter", "");

    }

    private Map<String, Object> missingData(State state) {
        logger.info("Missing information");
        return Map.of("missingParameter", "You didn't set or from/to currencies");
    }

    @Produces
    public CompiledGraph<State> buildGraph(BaseCheckpointSaver checkpointSaver) throws Exception {

        System.out.println(checkpointSaver);

        var compileConfig = CompileConfig.builder()
            .checkpointSaver(checkpointSaver)
            .interruptBefore("wait_for_human");

        var stateSerializer = new ObjectStreamStateSerializer<>( State::new );
        stateSerializer.mapper()
            .register(ChatMessage.class, new ChatMesssageSerializer())
            .register(ToolExecutionRequest.class, new ToolExecutionRequestSerializer());

        return new StateGraph<>(State.SCHEMA,stateSerializer)
            .addEdge(StateGraph.START, "convert")
            .addNode("convert", node_async(this::convert) )
            .addConditionalEdges("convert",
                    edge_async(this::isHumanInteractionRequired),
                    EdgeMappings.builder()
                        .to("invoke_tool", "tool")
                        .to("missing_data", "human")
                    .build()
            )
            .addNode("invoke_tool", node_async(this::invokeTool))
            .addEdge("invoke_tool", StateGraph.END)
            .addNode("missing_data", node_async(this::missingData))
            .addNode("wait_for_human", node_async(this::human_invoke))
            .addEdge("missing_data", "wait_for_human")
            .addEdge("wait_for_human", "convert")

            .compile(compileConfig.build());
    }


}
