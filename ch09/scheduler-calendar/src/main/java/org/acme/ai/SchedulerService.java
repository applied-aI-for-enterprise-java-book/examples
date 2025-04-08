package org.acme.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RegisterAiService(tools = SchedulerTools.class)
public interface SchedulerService {

    @SystemMessage("""
            You are a scheduling agent to help users to book meetings checking 
            for any conflict between the attendees and providing some suggestions 
            of the best time for the meeting according to some conditions.
            
            You will be provided with some tools to get the scheduled events for a user.  
            """)
    @UserMessage("""
            You need to schedule a meeting with the given person 
            in the given time range provided by the user.
            
            Time ranges might be expressed as morning, afternoon, or evening.
            Morning is from 8am to 12pm, Afternoon is from 12pm to 14pm, and evening is from 14pm to 18pm.
            
            It is important to check the conflicts and provide a date that meets the provided criteria
            but also not conflicting with any other existing event for the person.
            Duration of the meetings are important to correctly schedule them and avoid overlapping.
            
            Some examples of queries might be:
            
            - Book a meeting with John next week in the morning for 30 minutes.
            - Book a meeting with John for tomorrow evening fir 1 hour.
            - Book a meeting with John on 3rd of May of 2025 in the afternoon for 2 hours.
            
            You are provided with the user to book the meeting, the time range, and also the current date.
           
            With current week number you can get the correct wek number the user is asking to book the meeting.            
           
            Send only three possible suggestions for the event date,  
            expressed in the day, month and year, together with hour and minutes.
            Check the durations of the already booked events and the duration of the event to schedule correctly
            
            If it is not possible to find a time to schedule a meeting, return that you couldn't find any available slot.
            ----
            Current date is: {{current_date}}.
            
            The input is: {{message}}
            """)
    Events schedule(@V("message") String message,
                                 @V("current_date") LocalDate currentDate);
}
