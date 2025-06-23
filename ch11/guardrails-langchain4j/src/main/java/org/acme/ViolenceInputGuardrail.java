package org.acme;

import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.guardrail.InputGuardrail;
import dev.langchain4j.guardrail.InputGuardrailRequest;
import dev.langchain4j.guardrail.InputGuardrailResult;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;

public class ViolenceInputGuardrail implements InputGuardrail {

    private final ChatModel guardianModel;

    public ViolenceInputGuardrail() {
        this.guardianModel = ModelCreator.getGuardianModel();
    }

    @Override
    public InputGuardrailResult validate(InputGuardrailRequest params) {
        UserMessage userMessage = params.userMessage();

        SystemMessage systemMessage =
                SystemMessage.systemMessage("violence");
        ChatResponse chat = guardianModel.chat(systemMessage, userMessage);

        String result = chat.aiMessage().text();

        if ("no".equals(result.trim())) {
            return this.success();
        } else {
            return this.failure("Given input contains violent content");
        }

    }
}
