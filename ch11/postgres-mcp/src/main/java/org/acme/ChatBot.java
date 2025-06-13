package org.acme;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;

@RegisterAiService
@SystemMessage("""
    You have tools to interact with database and the users
    will ask you to perform operations like finding information in the database.
    
    You will need to transform the natural language message to SQL queries.
    The table with user information is named "person".
    """)
public interface ChatBot {
    @McpToolBox("postgres")
    PersonsDto chat(@UserMessage String message);
}
