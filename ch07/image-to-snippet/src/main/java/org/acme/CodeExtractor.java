package org.acme;

import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.ImageUrl;
import io.quarkiverse.langchain4j.RegisterAiService;

import java.net.URI;

@RegisterAiService
public interface CodeExtractor {

    @UserMessage("""
            This is image was reported on a GitHub issue.
            If this is a snippet of Java code, please respond
            with only the Java code.
            If the lines are numbered, removes them from the output.
            If it is not Java code, respond with 'NOT AN IMAGE'
            """)
    String extract(@ImageUrl URI image);

}
