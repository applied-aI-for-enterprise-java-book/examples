package org.acme;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import java.io.IOException;
import java.util.List;

public class MainApp {
    public static void main(String[] args) throws IOException {

        StreamingChatModel model = OpenAiStreamingChatModel.builder()
            .apiKey("demo")
            .baseUrl("http://langchain4j.dev/demo/openai/v1")
            .modelName("gpt-4o-mini")
            .build();

        Assistant assistant = AiServices.create(Assistant.class, model);

        TokenStream tokenStream = assistant.chat("Where is located DisneyLand Paris?");

        tokenStream.onPartialResponse((String partialResponse) -> System.out.print(partialResponse))
            .onRetrieved((List<Content> contents) -> System.out.println(contents))
            .onToolExecuted((ToolExecution toolExecution) -> System.out.println(toolExecution))
            .onCompleteResponse((ChatResponse response) -> System.out.println(response))
            .onError((Throwable error) -> error.printStackTrace())
            .start();

        System.in.read();

        model.chat("Where is located DisneyLand Paris?", new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.print(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                System.out.println("onCompleteResponse: " + completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });

        System.in.read();
    }
}
