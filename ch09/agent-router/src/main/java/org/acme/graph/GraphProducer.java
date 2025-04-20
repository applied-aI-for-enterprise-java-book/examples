package org.acme.graph;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.Map;
import java.util.Optional;
import org.acme.ai.AssistantService;
import org.acme.ai.BackToTheFutureService;
import org.acme.ai.QuestionRouter;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.state.AgentState;

import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@ApplicationScoped
public class GraphProducer {

    @Inject
    QuestionRouter questionRouter;

    @Inject
    BackToTheFutureService backToTheFutureService;

    @Inject
    AssistantService assistantService;

    public static class State extends AgentState {

        public State(Map<String, Object> initData) {
            super(initData);
        }

        public String question() {
            Optional<String> result = value("question");
            return result.orElseThrow( () -> new IllegalStateException( "question is not set!" ) );
        }

        public String generation() {
            return (String) value("generation").orElse("");
        }

    }

    @Produces
    public CompiledGraph<State> buildGraph() throws Exception {
        return new StateGraph<>(State::new)
            // Define the nodes
            .addConditionalEdges(StateGraph.START,
                edge_async(this::routeQuestion),
                Map.of(
                    QuestionRouter.Type.GENERAL.nodeName, "assistant",
                    QuestionRouter.Type.EMBEDDING.nodeName, "retrieve_rag"
                ))
            .addNode("assistant", node_async(this::assist) )
            .addNode("retrieve_rag", node_async(this::retrieve) )
            .addEdge("assistant", StateGraph.END)
            .addEdge("retrieve_rag", StateGraph.END)

            .compile();
    }

    private Map<String,Object> assist(State state) {
        String question = state.question();

        final String answer = assistantService.assist(question);
        return Map.of("generation", answer);
    }

    private Map<String,Object> retrieve(State state) {

        String question = state.question();

        final String answer = backToTheFutureService.generateAnswer(question);
        return Map.of( "generation", answer);
    }

    private String routeQuestion(State state) {

        String question = state.question();
        return questionRouter.routeToNode(question);

    }

}
