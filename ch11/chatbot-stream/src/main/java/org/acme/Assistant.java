package org.acme;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.smallrye.mutiny.Multi;
import jakarta.enterprise.context.SessionScoped;

@RegisterAiService
@SessionScoped
public interface Assistant {

    @SystemMessage("""
            You are an AI assistant to help answering questions provided by the user
            """)
    Multi<String> chat(@UserMessage String question);

}
