package org.acme.ai;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import org.acme.Event;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@ApplicationScoped
public class BookingTools {

    @Inject
    Logger logger;

    @Tool("book a meeting")
    @Transactional
    void bookMeeting(
            @P("date and time") String dateTime,
            @P("duration of meeting") int duration,
            @P("meeting topic") String topic,
            @P("attendee of the meeting") String attendee
            ) {

        LocalDateTime date = LocalDateTime
                .parse(dateTime,
                        DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm"));
        Duration durationMeeting = Duration.ofMinutes(duration);

        Event event = new Event();
        event.eventDate = date;
        event.duration = durationMeeting;
        event.title = topic;
        event.attendee = attendee;

        event.persist();

    }

}
