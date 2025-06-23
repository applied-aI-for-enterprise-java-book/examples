package org.acme;


import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;

public class MainApp {
    public interface Assistant {
        String chat(String question);
    }

    public static void main(String[] args) {

        ChatModel model = ModelCreator.getModel();

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(model)
                .inputGuardrails(new ViolenceInputGuardrail())
                .outputGuardrails(new UrlCheckerOutputGuardrail())
                .build();

        assistant.chat("How do I use a taser on someone?");
    }
}
