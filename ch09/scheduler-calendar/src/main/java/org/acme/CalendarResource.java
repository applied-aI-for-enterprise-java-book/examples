package org.acme;

import io.quarkus.runtime.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.ai.EventTime;
import org.acme.ai.SchedulerService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Path("/calendar")
public class CalendarResource {

    @Startup
    @Transactional
    public void createEvents() {
        LocalDate now = LocalDate.now();
        LocalDate tomorrow = now.plusDays(1);
        LocalDate nextWeek = now.plusWeeks(1);

        LocalTime morning = LocalTime.of(10, 0);
        LocalTime afternoon = LocalTime.of(13, 0);
        LocalTime evening = LocalTime.of(16, 0);

        Event event1 = new Event();
        event1.attendee = "Ada";
        event1.duration = Duration.ofHours(1);
        event1.title = "Meeting 1";
        event1.eventDate = LocalDateTime.of(tomorrow, morning);

        Event event2 = new Event();
        event2.attendee = "Ada";
        event2.duration = Duration.ofHours(1);
        event2.title = "Meeting 2";
        event2.eventDate = LocalDateTime.of(tomorrow, afternoon);

        Event event3 = new Event();
        event3.attendee = "Ada";
        event3.duration = Duration.ofHours(1);
        event3.title = "Meeting 3";
        event3.eventDate = LocalDateTime.of(nextWeek, evening);

        event1.persist();
        event2.persist();
        event3.persist();

    }

    public record Meeting(String attendee, String duration, String date, String title){}

    @GET
    public List<Meeting> allMeetings() {
        return Event.findAllEventsSortedByDate()
                .stream()
                .map(e -> new Meeting(e.attendee,
                        e.duration.toMinutes() + "m",
                        formatDate(e.eventDate),
                        e.title
                        ))
                .toList();
    }

    private String formatDate(LocalDateTime localDateTime) {
        return localDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + "-"
                + localDateTime.format(DateTimeFormatter.ISO_LOCAL_TIME);
    }

    @Inject
    SchedulerService schedulerService;


    @GET
    @Path("/1")
    public List<String> hello() {
        return schedulerService
                .schedule("Can you book a meeting for tomorrow evening with Ada for 30 minutes?",
                        LocalDate.now())
                .events()
                .stream().map(EventTime::toString)
                .toList();
    }

    @GET
    @Path("/2")
    public List<String> hello2() {
        return schedulerService
                .schedule("Can you book a meeting for the next week in the morning with Ada for 30 minutes?",
                        LocalDate.now())
                .events()
                .stream().map(EventTime::toString)
                .toList();
    }
}
