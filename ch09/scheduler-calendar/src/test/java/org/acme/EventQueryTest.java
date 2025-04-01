package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class EventQueryTest {

    @Test
    public void shouldReturnEventsByDay() {

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        List<Event> eventsByDay = Event.findEventsByDay(tomorrow);

        assertThat(eventsByDay).hasSize(2);

    }

}
