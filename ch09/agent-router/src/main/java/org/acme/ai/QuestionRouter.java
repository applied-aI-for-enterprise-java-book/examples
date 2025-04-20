package org.acme.ai;

import dev.langchain4j.model.output.structured.Description;
import dev.langchain4j.service.SystemMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

@ApplicationScoped
public class QuestionRouter {

    public enum Type { // <1>

        EMBEDDING("embedding"),
        GENERAL("general");

        public final String nodeName;

        Type(String node) {
            this.nodeName = node;
        }
    }

    class Route { // <2>

        @Description("Given a user question choose to route "
            + "it to general or a embedding.")
        Type nextNode;
    }

    @Singleton
    @RegisterAiService(retrievalAugmentor = RegisterAiService.NoRetrievalAugmentorSupplier.class)
    public interface Service {

        @SystemMessage("""
            You are an expert at routing a user question to a embedding store 
            or to a general model.
             
            The embedding store contains documents 
            related to Back To the Future DeLorean DMC-12 car.
            Use the embedding for questions related to 
            back to the future movies, DeLorean car, and costs. 
            
            Return general when the question is not related 
            to Back To The Future or the DeLorean Car.
            """)
        Route route(String question); // <3>

    }

    @Inject Service service; // <4>

    public String routeToNode(String query) { // <5>
        return service.route(query).nextNode.nodeName;
    }

}
