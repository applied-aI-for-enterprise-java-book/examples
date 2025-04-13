package org.acme.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService(tools = BookingTools.class)
public interface BookingService {

    @SystemMessage("""
            You are a scheduling agent to help users to book meetings.
            You'll be provided with a tool to save the meeting into the system.
            
            The meeting date is in the day/month/year-hour:minutes format, the duration is in minutes. 
            """)
    @UserMessage("""
            Book the following meeting in the system.
            Return if you could create it or not.
            
            Meeting: {{meeting}} for the date {{dateTime}} with topic {{topic}}
            """)
    String book(String meeting, String dateTime, String topic);
}
