package org.acme.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import jakarta.enterprise.context.SessionScoped;
import org.acme.data.RideRepository;
import org.acme.times.WaitingTime;


@RegisterAiService
@SessionScoped
public interface ThemeParkChatBot {

    @SystemMessage("""
        You are an assistant for answering questions about a theme park.

        These questions can only be related to theme park.
        Example of these questions can be:

        - Can you describe a given ride?
        - What is the minimum height to enter to a ride?
        - What rides can I access with my height?
        - What is the best ride at the moment?
        - What is the waiting time for a given ride? 
            
        If questions are not about theme park or you don't know the answer, 
        you should return always "I don't know".
        Don't give information that it is wrong
    """)
    @UserMessage("""
        The theme park user has the following question: {question}
        
        The answer must be max 2 lines.
        """)
    @ToolBox({RideRepository.class, WaitingTime.class})
    String chat(String question);

}
