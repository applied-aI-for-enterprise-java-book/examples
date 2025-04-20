document.addEventListener('DOMContentLoaded', function() {
    const chatMessages = document.getElementById('chat-messages');
    const userInput = document.getElementById('user-input');
    const sendButton = document.getElementById('send-button');

    // Display welcome message when chat loads
    setTimeout(() => {
        addBotMessage("Hello! I'm your AI assistant. How can I help you today?");
    }, 500);

    // Send message when button is clicked
    sendButton.addEventListener('click', sendMessage);

    // Send message when Enter key is pressed
    userInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });

    function sendMessage() {
        const message = userInput.value.trim();
        if (message === '') return;

        // Add user message to chat
        addUserMessage(message);
        userInput.value = '';

        // Show typing indicator
        showTypingIndicator();

            // Remove typing indicator
            removeTypingIndicator();

            const response = getBotResponse(message)
            response.then((data) => {
              // Add bot response to chat
              addBotMessage(data.reply);

              // Scroll to bottom of chat
              scrollToBottom();
            });

    }

    function addUserMessage(text) {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message user-message';

        const time = getCurrentTime();
        messageDiv.innerHTML = `
            <div style="display: flex; align-items: center; margin-bottom: 5px;">
                <div class="message-avatar user-avatar">U</div>
                <span style="font-weight: bold;">You</span>
            </div>
            <div>${text}</div>
            <div class="message-time">${time}</div>
        `;

        chatMessages.appendChild(messageDiv);
        scrollToBottom();
    }

    function addBotMessage(text) {
        const messageDiv = document.createElement('div');
        messageDiv.className = 'message bot-message';

        const time = getCurrentTime();
        messageDiv.innerHTML = `
            <div style="display: flex; align-items: center; margin-bottom: 5px;">
                <div class="message-avatar bot-avatar-small">AI</div>
                <span style="font-weight: bold;">Assistant</span>
            </div>
            <div>${text}</div>
            <div class="message-time">${time}</div>
        `;

        chatMessages.appendChild(messageDiv);
        scrollToBottom();
    }

    function showTypingIndicator() {
        const typingDiv = document.createElement('div');
        typingDiv.className = 'typing-indicator';
        typingDiv.id = 'typing-indicator';
        typingDiv.innerHTML = `
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
            <div class="typing-dot"></div>
        `;

        chatMessages.appendChild(typingDiv);
        scrollToBottom();
    }

    function removeTypingIndicator() {
        const typingIndicator = document.getElementById('typing-indicator');
        if (typingIndicator) {
            typingIndicator.remove();
        }
    }

    function scrollToBottom() {
        chatMessages.scrollTop = chatMessages.scrollHeight;
    }

    function getCurrentTime() {
        const now = new Date();
        let hours = now.getHours();
        let minutes = now.getMinutes();
        const ampm = hours >= 12 ? 'PM' : 'AM';

        hours = hours % 12;
        hours = hours ? hours : 12; // the hour '0' should be '12'
        minutes = minutes < 10 ? '0' + minutes : minutes;

        return `${hours}:${minutes} ${ampm}`;
    }

    async function getBotResponse(userMessage) {
        try {
            const response = await fetch('http://localhost:8080/chat', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    message: userMessage
                })
            });

            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            return data;
        } catch (error) {
            console.error('Error fetching bot response:', error);
            return "I'm having trouble connecting to the server. Please try again later.";
        }
    }

});