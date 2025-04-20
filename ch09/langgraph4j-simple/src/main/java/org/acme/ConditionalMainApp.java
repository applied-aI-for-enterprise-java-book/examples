package org.acme;

import java.util.Map;
import java.util.Optional;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.state.AgentState;

import static org.bsc.langgraph4j.action.AsyncEdgeAction.edge_async;
import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

public class ConditionalMainApp {

    public static class State extends AgentState { // <1>

        public State(Map<String, Object> initData) {
            super(initData);
        }

        public Optional<Integer> age() { // <2>
            return value("age");
        }

        public Optional<String> message() { // <3>
            return value("message");
        }

    }

    private Map<String, Object> setsMessage(State state) {
        System.out.println(state.age().get()); // <3>
        return Map.of("message", "Current age " + state.age().get());
    }

    private Map<String, Object> surroundMessage(State state) {
        return Map.of("message", "<" + state.message().get() + ">");
    }

    public CompiledGraph<State> createGraph() throws GraphStateException {
        return new StateGraph<>(State::new)
            .addNode("createMessage", node_async(this::setsMessage))
            .addEdge(StateGraph.START, "createMessage")
            .addNode("toUpperCase", node_async(state ->
                Map.of("message", state.message().get().toUpperCase())))
            .addNode("surroundCase", node_async(this::surroundMessage))
            .addConditionalEdges("createMessage",
                edge_async(state ->
                    state.age().get() >= 18 ? "adult" : "minor"),
                Map.of("minor", "surroundCase",
                    "adult", "toUpperCase"))
            .addEdge("toUpperCase", StateGraph.END)
            .addEdge("surroundCase", StateGraph.END)
            .compile();
    }

    public static void main(String[] args) throws GraphStateException {

        ConditionalMainApp mainApp = new ConditionalMainApp();

        final CompiledGraph<State> graph = mainApp.createGraph();
        final Optional<State> finalState = graph.invoke(Map.of("age", 44));
        final State state = finalState.get();
        System.out.println(state.message().get());

    }

}
