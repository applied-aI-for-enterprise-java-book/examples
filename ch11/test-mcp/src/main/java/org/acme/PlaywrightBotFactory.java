package org.acme;

import dev.langchain4j.mcp.McpToolProvider;
import dev.langchain4j.mcp.client.DefaultMcpClient;
import dev.langchain4j.mcp.client.McpClient;
import dev.langchain4j.mcp.client.transport.McpTransport;
import dev.langchain4j.mcp.client.transport.stdio.StdioMcpTransport;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class PlaywrightBotFactory {

    public static McpClient createMcpClient() {
        McpTransport transport = new StdioMcpTransport.Builder()
                .command(List.of(
                        "npx",
                        "-y",
                        "@executeautomation/playwright-mcp-server"))
                .logEvents(true)
                .build();

        return new DefaultMcpClient.Builder()
                .transport(transport)
                .logHandler(mcpLogMessage -> System.out.println(mcpLogMessage.data()))
                .build();
    }

    public static PlaywrightBot createBot(McpClient client) throws IOException {
        OpenAiChatModel model = OpenAiChatModel.builder()
                .apiKey(Files.readString(Paths.get(".env")))
                .modelName("gpt-4o-mini")
                .build();

        ToolProvider toolProvider = McpToolProvider.builder()
                .mcpClients(List.of(client))
                .build();

        return AiServices.builder(PlaywrightBot.class)
                .chatLanguageModel(model)
                .toolProvider(toolProvider)
                .build();

    }

}
