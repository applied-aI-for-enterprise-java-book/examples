document.addEventListener("DOMContentLoaded", function () {
    const chatBox = document.getElementById("chat-box");
    const chatInput = document.getElementById("chat-input");
    const sendBtn = document.getElementById("send-btn");

    const socket = new WebSocket("ws://localhost:3000");

    sendBtn.addEventListener("click", sendMessage);
    chatInput.addEventListener("keypress", function (e) {
        if (e.key === "Enter") sendMessage();
    });

    function sendMessage() {
        const message = chatInput.value.trim();
        if (message === "") return;

        addMessageToChat("user", message);
        socket.send(JSON.stringify({ message }));

        chatInput.value = "";
    }

    function addMessageToChat(sender, content) {
        const messageDiv = document.createElement("div");
        messageDiv.classList.add("chat-message", sender === "user" ? "user-message" : "bot-message");

        if (sender === "bot" && content.includes("<")) {
            // If content contains HTML, render it
            messageDiv.innerHTML = content;
        } else {
            messageDiv.textContent = content;
        }

        chatBox.appendChild(messageDiv);
        chatBox.scrollTop = chatBox.scrollHeight;
    }

    socket.onmessage = function (event) {
        const data = JSON.parse(event.data);
        addMessageToChat("bot", data.message);
    };
});
