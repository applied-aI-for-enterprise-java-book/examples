package org.acme.triage_service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface TriageService {

    @SystemMessage("""
                Analyze the sentiment of the text below.
                Respond only with one word to describe the sentiment.
            """)
    @UserMessage("""
            Your task is to process the review delimited by ---.
            
            The possible sentiment values are 'POSITIVE' for positive sentiment or 
                'NEGATIVE' for negative sentiment, or 'NEUTRAL' if you cannot detect any sentiment 
            

            Some examples:
            - INPUT: This is fantastic news!
              OUTPUT: POSITIVE
                                                                             
            - INPUT: Pi is roughly equal to 3.14
              OUTPUT: NEUTRAL

            - INPUT: I really disliked the pizza. Who would use pineapples as a pizza topping?
              OUTPUT: NEGATIVE
              
            ---
            {{review}}
            ---
            """)
    Evaluation triage(String review);

}
