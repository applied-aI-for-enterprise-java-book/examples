package org.acme;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface Assistant {

    @SystemMessage("""
        You are an assistant to answer questions of users.
        
        Be polite and if say "I don't know" if user requests some information out of your knowledge
        """)
    String assist(@UserMessage String question);

}
