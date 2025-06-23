package org.acme;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;

public class ModelCreator {

    private static ChatModel guardianModel;
    private static ChatModel model;

    static {
        guardianModel = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .logRequests(true)
                .logResponses(true)
                .modelName("granite3-guardian:2b")
                .build();

        model = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("demo")
                .modelName("gpt-4o-mini")
                .build();
    }

    public static ChatModel getModel() {
        return model;
    }

    public static ChatModel getGuardianModel() {
        return guardianModel;
    }

}
