package org.acme;

import dev.langchain4j.mcp.client.McpClient;
import io.quarkiverse.langchain4j.mcp.runtime.McpClientName;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class MCPClientTest {

    @Inject
    @McpClientName("zodiac")
    McpClient mcpClient;

    @Test
    public void shouldListTools() {
        System.out.println(mcpClient.listTools());
    }

}
