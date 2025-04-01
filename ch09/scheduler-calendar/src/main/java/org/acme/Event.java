package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedNativeQuery;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NamedNativeQuery(name = "select_by_day",
        query = "SELECT * FROM event WHERE EXTRACT(DAY FROM eventDate) = :day AND EXTRACT(MONTH FROM eventDate) = :month AND EXTRACT(YEAR FROM eventDate) = :year;",
        resultClass = Event.class
)
@NamedNativeQuery(name = "select_by_week",
        query = """
        SELECT *
        FROM event
        WHERE WEEK(eventDate) = :week_number
        AND YEAR(eventDate) = :year;
        """,
        resultClass = Event.class
)
public class Event extends PanacheEntity {

    public String title;
    public String attendee;
    public LocalDateTime eventDate;
    public Duration duration;

    public static List<Event> findEventsByWeekOfYear(int weekNumber, int year) {
        return getEntityManager()
                .createNativeQuery("select_by_week", Event.class)
                .setParameter("week_number", weekNumber)
                .setParameter("year", year)
                .getResultList();
    }

    public static List<Event> findEventsByDay(LocalDate localDate) {
        return getEntityManager()
                .createNativeQuery("select_by_day", Event.class)
                .setParameter("day", localDate.getDayOfMonth())
                .setParameter("month", localDate.getMonthValue())
                .setParameter("year", localDate.getYear())
                .getResultList();
    }

}
