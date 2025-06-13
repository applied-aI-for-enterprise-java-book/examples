package org.acme;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface Bot {

    @SystemMessage("""
            You have tools to interact with the local filesystem and the users
            will ask you to perform operations like reading and writing files.
            """
    )
    String chat(@UserMessage String question);
}
