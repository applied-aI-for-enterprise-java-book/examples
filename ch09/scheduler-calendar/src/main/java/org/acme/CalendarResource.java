package org.acme;

import io.quarkus.runtime.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.ai.EventTime;
import org.acme.ai.SchedulerService;
import org.acme.graph.State;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.utils.CollectionsUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
        return localDateTime
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm"));
    }

    @Inject
    SchedulerService schedulerService;

    @Inject
    CompiledGraph<State> graph;

    public record Slots(List<String> slots){}

    @POST
    @Path("/getSlots")
    public Slots getSlots(String message) {

        var runnableConfig =  RunnableConfig.builder()
                .threadId(getUser())
                .build();

        final Optional<State> optionalState =
                graph.invoke(Map.of("query", message), runnableConfig);
        final State state = optionalState.get();

        return new Slots(state.currentSchedule());
    }

    public record Booking(String slot, String note){}

    public record BookingResult(boolean success, boolean reschedule, List<String> slots){}

    @POST
    @Path("/bookMeeting")
    public BookingResult bookMeeting(Booking booking) throws Exception {
        System.out.println(booking);

        var runnableConfig =  RunnableConfig.builder()
                .threadId(getUser())
                .build();

        var updateConfig = graph.updateState(runnableConfig,
                Map.of("currentSelection", booking.slot,
                        "meetingName", booking.note),
                null);

        final Optional<State> optionalState =
                graph.invoke(null, updateConfig);

        State state = optionalState.get();

        if (state.noValidSchedule()) {
            System.out.println("Reschedule");
            return new BookingResult(true,
                    state.noValidSchedule(),
                    state.currentSchedule());
        }

        return new BookingResult(true,
                false,
                Collections.emptyList());
    }

    // Testing

    @GET
    @Path("/1")
    public List<String> hello() {
        return schedulerService
                .schedule("Can you book a meeting for tomorrow evening with Ada for 30 minutes?",
                        LocalDate.now(), List.of())
                .events()
                .stream().map(EventTime::toString)
                .toList();
    }

    @GET
    @Path("/2")
    public List<String> hello2() {
        return schedulerService
                .schedule("Can you book a meeting for the next week in the morning with Ada for 30 minutes?",
                        LocalDate.now(), List.of())
                .events()
                .stream().map(EventTime::toString)
                .toList();
    }

    @GET
    @Path("/3")
    public List<String> hello3() {
        return schedulerService
                .schedule("Can you book a meeting for tomorrow evening with Ada for 30 minutes?",
                        LocalDate.now(), List.of("12/04/2025-15:00"))
                .events()
                .stream().map(EventTime::toString)
                .toList();
    }

    private String getUser() {
        return "alexandra@example.com";
    }
}
