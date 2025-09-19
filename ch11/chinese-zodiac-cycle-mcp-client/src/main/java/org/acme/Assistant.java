package org.acme;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;

@RegisterAiService
public interface Assistant {

    @SystemMessage("""
                You are an assistant to answer questions for any user.
                You have a function to calculate the chinese zodiacal animal from a given date.
                Use it all the time. 
                """)
    @McpToolBox("zodiac")
    String askMe(@UserMessage String message);
}
