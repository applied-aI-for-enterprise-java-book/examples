package org.acme;

import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.net.URI;

@Path("/extract")
public class ScreenShotResource {

    @Inject
    ChatLanguageModel chatLanguageModel;

    @Inject
    CodeExtractor codeExtractor;

    @GET
    @Path("/ai")
    @Produces(MediaType.TEXT_PLAIN)
    public String extractWithAiService() {
        return codeExtractor
                .extract(URI.create("https://i.postimg.cc/fL6x1MK9/screenshot.png"));
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String extract() {

            UserMessage userMessage = UserMessage.from(
                    TextContent.from(
                            "This is image was reported on a GitHub issue." +
                                    "If this is a snippet of Java code, please respond " +
                                    "with only the Java code. " +
                                    "If the lines are numbered, removes them from the output." +
                                    "If it is not Java code, respond with 'NOT AN IMAGE'"),
                    ImageContent.from(URI.create("https://i.postimg.cc/fL6x1MK9/screenshot.png"))
            );

            ChatResponse response = chatLanguageModel.chat(userMessage);
            return response.aiMessage().text();

    }
}
