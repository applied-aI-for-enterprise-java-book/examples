package org.acme;

import dev.langchain4j.mcp.client.McpClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

public class PlaywrightMainApp {

    public static void main(String[] args) throws Exception {

        McpClient mcpClient = PlaywrightBotFactory.createMcpClient();
        PlaywrightBot bot = PlaywrightBotFactory.createBot(mcpClient);

        try {
            List<ReportTest> reportTests = Files.list(Paths.get("src/main/resources"))
                    .filter(p -> p.toString().endsWith(".feature"))
                    .map(feature -> {

                        try {
                            String content = Files.readString(feature);

                            TestResult testResult = bot.chat(content);
                            return new ReportTest(feature, testResult);

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                    })
                    .toList();

            System.out.println(reportTests);

        } finally {
            mcpClient.close();
        }

    }
}
