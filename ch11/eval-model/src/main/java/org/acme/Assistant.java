package org.acme;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface Assistant {

    @SystemMessage("""
        You arean assistent to answer questions of users.
        
        Be polite and if say "I don't know" if user requests some information out of your   
        """)
    String assist(@UserMessage String question);

}
