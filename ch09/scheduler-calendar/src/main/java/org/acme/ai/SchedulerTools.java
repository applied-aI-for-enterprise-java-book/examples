package org.acme.ai;

import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.Event;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.List;

@ApplicationScoped
public class SchedulerTools {

    @Inject
    Logger logger;

    public record EventDto(LocalDateTime localDateTime, Duration duration){}

    @Tool("find meetings for the given week number of the year")
    public List<EventDto> findMeetingsByWeekNumber(
            @P("attendee name") String attendee,
            @P("week of the year to book a meeting") int weekOfTheYear) {

        logger.infof("Find by week %d for the user %s", weekOfTheYear, attendee);

        return Event.findEventsByWeekOfYear(attendee, weekOfTheYear, Year.now().getValue())
                .stream()
                .map(event -> new EventDto(event.eventDate, event.duration))
                .toList();
    }

    @Tool("find meetings for the given day")
    public List<EventDto> findMeetingsByDay(
            @P("attendee name") String attendee,
            @P("year in the calendar") int year,
            @P("month in the calendar") int month,
            @P("day in the calendar") int day) {

        LocalDate localDate = LocalDate.of(year, month, day);

        logger.infof("Find by day %s for the user %s", localDate, attendee);

        return Event.findEventsByDay(attendee, localDate)
                .stream()
                .map(event -> new EventDto(event.eventDate, event.duration))
                .toList();

    }

}
