package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.panache.common.Sort;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedNativeQuery;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@NamedNativeQuery(name = "select_by_day",
        query = """
        SELECT * FROM event 
                WHERE EXTRACT(DAY FROM eventDate) = :day 
                AND EXTRACT(MONTH FROM eventDate) = :month 
                AND EXTRACT(YEAR FROM eventDate) = :year 
                AND attendee = :attendee;
        """,
        resultClass = Event.class
)
@NamedNativeQuery(name = "select_by_week",
        query = """
        SELECT *
        FROM event
        WHERE WEEK(eventDate) = :week_number
        AND YEAR(eventDate) = :year 
        AND attendee = :attendee;
        """,
        resultClass = Event.class
)
public class Event extends PanacheEntity {

    public String title;
    public String attendee;
    public LocalDateTime eventDate;
    public Duration duration;

    public static List<Event> findAllEventsSortedByDate() {
        return Event.listAll(Sort.by("eventDate"));
    }

    public static List<Event> findEventsByWeekOfYear(String attendee, int weekNumber, int year) {
        return getEntityManager()
                .createNamedQuery("select_by_week", Event.class)
                .setParameter("week_number", weekNumber)
                .setParameter("year", year)
                .setParameter("attendee", attendee)
                .getResultList();
    }

    public static List<Event> findEventsByDay(String attendee, LocalDate localDate) {
        return getEntityManager()
                .createNamedQuery("select_by_day", Event.class)
                .setParameter("day", localDate.getDayOfMonth())
                .setParameter("month", localDate.getMonthValue())
                .setParameter("year", localDate.getYear())
                .setParameter("attendee", attendee)
                .getResultList();
    }

}
