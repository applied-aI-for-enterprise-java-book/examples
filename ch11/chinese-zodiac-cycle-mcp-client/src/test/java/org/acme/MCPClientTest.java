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

    @Inject
    Assistant assistant;

    @Test
    public void shouldUseMcp() {
        System.out.println(
                assistant.askMe("Can you give me my chinese zodiacal animal if I was born on 4th of July of 1980?")
        );
    }

    @Inject
    @McpClientName("zodiac")
    McpClient mcpClient;

    @Test
    public void shouldListTools() {
        System.out.println(mcpClient.listTools());
    }

}
