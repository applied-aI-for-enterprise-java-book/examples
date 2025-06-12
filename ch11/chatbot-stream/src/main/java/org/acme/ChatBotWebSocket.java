package org.acme;

import dev.langchain4j.model.chat.StreamingChatModel;
import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import io.smallrye.mutiny.Multi;
import jakarta.inject.Inject;

@WebSocket(path = "/chatbot")
public class ChatBotWebSocket {

    @Inject
    StreamingChatModel model;

    private final Assistant assistant;

    public ChatBotWebSocket(Assistant assistant) {
        this.assistant = assistant;
    }

    @OnOpen
    public String onOpen() {
        System.out.println(model);
        return
            "Hello, I'm a Bot, how can I help you?";
    }

    @OnTextMessage
    public Multi<String> onMessage(String message) {
        return assistant.chat(message);
    }

}
