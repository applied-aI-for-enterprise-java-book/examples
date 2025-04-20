package org.acme.ai;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.jboss.logging.Logger;

@ApplicationScoped
public class AssistantService {

    @Inject
    Logger logger;

    @Singleton
    @RegisterAiService(retrievalAugmentor =
        RegisterAiService.NoRetrievalAugmentorSupplier.class)
    public interface Service {
        @UserMessage(""" 
            You are an assistant for question-answering tasks. 
            Use the following pieces of retrieved context to answer the question. 
            If you don't know the answer, just say that you don't know. 
            Use three sentences maximum and keep the answer concise.
            
            Question: {{question}} 
            """)
        String generate(String question);
    }

    @Inject
    Service service;

    public String assist(String question) {

        logger.info("AssistantService");

        return service.generate(question);
    }

}
