package org.acme;

import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.mcp.runtime.McpClientName;
import io.quarkiverse.langchain4j.mcp.runtime.McpToolBox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class MCPClientTest {

    /**@RegisterAiService
    interface Assistant {

        @SystemMessage("""
                You are an assistant to answer questions for any user.
                You have a function to calculate the chinese zodiacal animal from a given date. 
                """)
        @McpToolBox("zodiac")
        String askMe(@UserMessage  String message);
    }**/

    @Inject
    @McpClientName("zodiac")
    McpClient mcpClient;

    @Test
    public void shouldListTools() {
        System.out.println(mcpClient.listTools());
    }

}
