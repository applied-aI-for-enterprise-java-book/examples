package org.acme;

import dev.langchain4j.agent.tool.ToolSpecification;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.http.HttpMcpTransport;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class LangChain4jMcpClientTest {

    @TestHTTPResource
    URL url;

    private McpClient mcpClient;

    @BeforeEach
    void setUpMcpClient() {
        mcpClient = new DefaultMcpClient.Builder()
                .clientName("test-mcp-client-zodiac")
                .toolExecutionTimeout(Duration.ofSeconds(10))
                .transport(new HttpMcpTransport.Builder().sseUrl(url.toString() + "mcp/sse").build())
                .build();
    }

    @AfterEach
    void closeClient() throws Exception {
        this.mcpClient.close();
    }

    @Test
    public void shouldListTools() {
        List<ToolSpecification> toolSpecifications = mcpClient.listTools();
        assertThat(toolSpecifications).hasSize(1);

    }

}
