package org.acme;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.guardrail.OutputGuardrail;
import dev.langchain4j.guardrail.OutputGuardrailRequest;
import dev.langchain4j.guardrail.OutputGuardrailResult;


public class UrlCheckerOutputGuardrail implements OutputGuardrail {

    @Override
    public OutputGuardrailResult validate(OutputGuardrailRequest params) {

        AiMessage aiMessage = params.responseFromLLM().aiMessage();
        String msg = aiMessage.text();

        if (URLChecker.areLinksReachable(msg)) {
            return success();
        } else {
            return retry("There are some URLs that are not reachable");
        }
    }
}
