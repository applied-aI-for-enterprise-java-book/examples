package org.acme.graph;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.acme.ai.BookingService;
import org.acme.ai.Events;
import org.acme.ai.SchedulerService;
import org.bsc.langgraph4j.CompileConfig;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.checkpoint.BaseCheckpointSaver;
import org.bsc.langgraph4j.checkpoint.MemorySaver;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@ApplicationScoped
public class HumanGraphProducer {

    @Produces
    @Singleton
    BaseCheckpointSaver memorySaver() {
        return new MemorySaver();
    }

    private String formatDate(LocalDateTime localDateTime) {
        return localDateTime
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm"));
    }

    @Inject
    SchedulerService schedulerService;

    private Map<String, Object> findSlots(State state) {
        System.out.println("**** Find Slots ****");
        System.out.println("Forbidden Slots " + state.notValid());
        String query = state.query();

        Events events = schedulerService
                .schedule(query, LocalDate.now(), state.notValid());

        List<String> possibleDates = events.events()
                .stream()
                .map(e ->
                        LocalDateTime.of(e.year(), e.month(), e.day(),
                                        e.hour(), e.minutes()))
                .map(this::formatDate)
                .toList();

        return Map.of("current", possibleDates);
    }

    private Map<String, Object> appendNotValid(State state) {

        System.out.println("**** Append Not Valid ****");

        List<String> notValid = new ArrayList<>(state.notValid());
        notValid.addAll(state.currentSchedule());

        return Map.of("notvalid", notValid);
    }

    private String conditionToSurround(State state) {

        System.out.println("**** Condition **** " + state.noValidSchedule());

        System.out.println(state.currentSelection());

        return state.noValidSchedule() ? "another" : "save";
    }

    @Inject
    BookingService bookingService;

    private Map<String, Object> saveMeeting(State state) {

        bookingService.book(state.query(),
                state.currentSelection(),
                state.meetingName());

        return Collections.EMPTY_MAP;

    }

    private Map<String, Object> humanFeedback(State state) {
        return Map.of();
    }

    @Produces
    @Singleton
    CompiledGraph<State> createHumanGraph(BaseCheckpointSaver checkpointSaver) throws GraphStateException {

        var compileConfig = CompileConfig.builder()
                .checkpointSaver(checkpointSaver)
                .interruptBefore("humanFeedback");

        return new StateGraph<>(State::new)
                .addEdge(StateGraph.START,"findSlots")
                .addNode("findSlots", node_async(this::findSlots))
                .addEdge("findSlots", "humanFeedback")
                .addNode("humanFeedback", node_async(this::humanFeedback))
                .addConditionalEdges("humanFeedback", edge_async(this::conditionToSurround),
                        Map.of("save", "saveMeeting", "another", "appendNotValidDates"))
                .addNode("appendNotValidDates", node_async(this::appendNotValid))
                .addEdge("appendNotValidDates", "findSlots")
                .addNode("saveMeeting", node_async(this::saveMeeting))
                .addEdge("saveMeeting", StateGraph.END)
                .compile(compileConfig.build());

    }
}
