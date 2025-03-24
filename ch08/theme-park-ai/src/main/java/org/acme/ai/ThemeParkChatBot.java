package org.acme.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkiverse.langchain4j.ToolBox;
import jakarta.enterprise.context.SessionScoped;
import org.acme.data.RideRepository;
import org.acme.rag.RidesRetrievalAugmentator;
import org.acme.times.WaitingTime;


@RegisterAiService(retrievalAugmentor = RidesRetrievalAugmentator.class)
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
        - What options I have to arrive to the theme park?
            
        If you need the location of the theme park to answer a question, 
        the theme park is located at Barcelona.    
        
        Don't give information that it is wrong.
    """)
    @UserMessage("""
        The theme park is located at Barcelona.
        
        The theme park user has the following question: {question}
        
        The answer must be max 2 lines.
        """)
    @ToolBox({RideRepository.class, WaitingTime.class})
    String chat(String question);

}
