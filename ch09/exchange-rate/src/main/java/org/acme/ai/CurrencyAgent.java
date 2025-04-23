package org.acme.ai;

import com.fasterxml.jackson.core.JsonProcessingException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.agent.tool.ToolSpecifications;
import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class CurrencyAgent {

    @Inject
    ChatLanguageModel model;

    @Inject
    ExchangeRateTool exchangeRateTool;

    @Inject
    ObjectMapper mapper;

    public AiMessage exchangeRate(SystemMessage systemMessage, UserMessage userMessage) throws JsonProcessingException {
       ChatRequest request = ChatRequest.builder()
           .messages(
               systemMessage,
               userMessage
           )
           .toolSpecifications(ToolSpecifications.toolSpecificationsFrom(ExchangeRateTool.class))
           .build();

       ChatResponse response = model.chat(request);
       return response.aiMessage();

    }

    public AiMessage invokeTool(List<ChatMessage> messages, ToolExecutionRequest toolExecutionRequest) throws JsonProcessingException {
        // {"currencyFrom":"USD","currencyTo":"INR"}
        final Map<String, String> arguments = mapper.readValue(
            toolExecutionRequest.arguments(), Map.class);

        final JsonNode exchangeRate = exchangeRateTool
            .getExchangeRate(
                arguments.get("currencyFrom"),
                arguments.get("currencyTo"));

        ToolExecutionResultMessage toolExecutionResultMessage =
            ToolExecutionResultMessage.from(toolExecutionRequest,
                mapper.writeValueAsString(exchangeRate));

        final List<ChatMessage> chatMessages = new ArrayList<>(messages);
        chatMessages.add(toolExecutionResultMessage);

        ChatRequest request = ChatRequest.builder()
            .messages(chatMessages)
            .toolSpecifications(
                ToolSpecifications.toolSpecificationsFrom(ExchangeRateTool.class)
            )
            .build();

        return model.chat(request).aiMessage();

    }
}
