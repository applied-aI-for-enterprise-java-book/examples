package org.acme.triage_service;

import java.util.HashMap;
import java.util.Map;

import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TriageServiceController {

    @Autowired
    TriageService triageService;

    @Autowired
    ChatModel chatLanguageModel;


    @GetMapping("/capital")
    public String capital() {
        return chatLanguageModel.chat("What is the capital of Madagascar?");
    }

    @GetMapping("/triage")
    public Map<String, Evaluation> chat() {

        Map<String, Evaluation> result = new HashMap<>();

        String claim = "I love the service you offer";

        Evaluation triage = triageService.triage(claim);

        System.out.println(claim);
        System.out.println(triage);

        result.put(claim, triage);

        claim = "I couldn't resolve my problem, "
            + "I need to wait for 2 hours to be attended and no solution yet,"
            + "the service is horrible. I hate your bank";

        Evaluation triage2 = triageService.triage(claim);

        System.out.println(claim);
        System.out.println(triage2);

        result.put(claim, triage2);

        return result;
    }

}
