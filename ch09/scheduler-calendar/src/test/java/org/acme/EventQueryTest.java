package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.time.temporal.WeekFields;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class EventQueryTest {

    @Test
    public void shouldReturnEventsByDay() {

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Event> eventsByDay = Event.findEventsByDay("Ada", tomorrow);

        assertThat(eventsByDay).hasSize(2);

    }

    @Test
    public void shouldReturnEventsByWeek() {
        LocalDate nextWeek = LocalDate.now().plusWeeks(1);
        int weekNumber = nextWeek.get(WeekFields.ISO.weekOfWeekBasedYear());

        List<Event> events = Event.findEventsByWeekOfYear("Ada",
                weekNumber, Year.now().getValue());
        assertThat(events).hasSize(1);
    }

}
