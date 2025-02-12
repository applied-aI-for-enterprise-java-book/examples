package org.acme;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.request.ResponseFormat;
import dev.langchain4j.model.chat.request.ResponseFormatType;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.output.JsonSchemas;

public class MainApp {

    public enum Label {
        PERSISTENCE, UI, EVENT, GENERIC // <1>
    }

    public record IssueClassification(Label category){}

    @SystemMessage("""
        You are bot in charge of categorizing issues from a bug tracker. 
    """) // <1>
    public interface LabelDetector {
        @UserMessage("""
        Analyze the provided issue and categorize into one of the category.
        The issues opened are for Java projects so you can expect some Java acronyms,
        use them to categorize the issues as well.
        
        The possible values for a category must be PERSISTENCE, UI, EVENT or GENERIC.
        
        In case of not knowing how to categorize use the GENERIC label.
        
        Some examples of you might find:
        
        INPUT: Entity is not persisted
        OUTPUT: PERSISTENCE
        
        INPUT: JPA is failing to configure entities
        OUTPUT: PERSISTENCE
        
        INPUT: The element is not visible in the web
        OUTPUT: UI 
        
        INPUT: The event is sent but never received
        OUTPUT: EVENT
        
        INPUT: Kafka streaming is failing in some circumstances
        OUTPUT: EVENT
        
        INPUT: java.lang.NullPointerException in a request
        OUTPUT: GENERIC
        
        INPUT: {{issueTitle}}
        OUTPUT:
        """) // <2>
        IssueClassification categorizeIssue(@V("issueTitle") String issueTitle); // <3>
    }

    public static void main(String[] args) {

        ChatLanguageModel model = GoogleAiGeminiChatModel.builder() // <1>
            .apiKey(System.getProperty("API_KEY")) // <2>
            .modelName("gemini-1.5-flash") // <3>
            .responseFormat(ResponseFormat.builder() // <4>
                .type(ResponseFormatType.JSON)
                .jsonSchema(JsonSchemas.jsonSchemaFrom(IssueClassification.class).get())
                .build())
            .build();

        LabelDetector labelDetector = AiServices.builder(LabelDetector.class)
            .chatLanguageModel(model)
            .build();

        IssueClassification label1 = labelDetector
            .categorizeIssue("When storing a user in the database, "
                + "it throws an exception");

        System.out.println(label1);

        IssueClassification label2 = labelDetector
            .categorizeIssue("JDBC connection exception thrown"); // <5>

        System.out.println(label2);

        IssueClassification label3 = labelDetector
            .categorizeIssue("Math operation fails when divide by 0");

        System.out.println(label3);

    }
}
