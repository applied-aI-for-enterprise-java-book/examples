package org.acme;

import io.quarkus.websockets.next.OnOpen;
import io.quarkus.websockets.next.OnTextMessage;
import io.quarkus.websockets.next.WebSocket;
import jakarta.inject.Inject;
import org.acme.ai.ThemeParkChatBot;

@WebSocket(path = "/chat")
public class WebSocketChatBot {

    @Inject
    ThemeParkChatBot themeParkChatBot;

    @OnOpen
    public String onOpen() {
        return  "Hello, how can I help you?";
    }

    @OnTextMessage
    public String onMessage(String message) {
        return themeParkChatBot.chat(message);
    }
}
