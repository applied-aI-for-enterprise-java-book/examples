package org.acme;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

public interface PlaywrightBot {

    @SystemMessage("""
            You have tools to interact with the playwright testing framework and the users
            will ask you to perform operations like navigating to a location, filling a form, 
            or get information from a web page to validate.
            
            You need to close the browser when you finish processing the test.
            """)
    TestResult chat(@UserMessage String question);

}
