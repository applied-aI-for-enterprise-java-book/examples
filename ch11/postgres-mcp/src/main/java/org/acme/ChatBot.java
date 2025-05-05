package org.acme;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import java.util.List;

@RegisterAiService
@SystemMessage("""
    You have tools to interact with database and the users
    will ask you to perform operations like finding information in the database.
    
    You will need to transform the natural language message to SQL queries.
    """)
public interface ChatBot {
    PersonsDto chat(@UserMessage String message);
}
