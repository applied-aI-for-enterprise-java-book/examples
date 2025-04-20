package org.acme;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.GraphStateException;
import org.bsc.langgraph4j.StateGraph;
import org.bsc.langgraph4j.state.AgentState;
import org.bsc.langgraph4j.state.AppenderChannel;
import org.bsc.langgraph4j.state.Channel;
import org.bsc.langgraph4j.state.Channels;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

public class AppenderMainApp {


    public static class AppenderState extends AgentState {

        static Map<String, Channel<?>> SCHEMA = Map.of(
            "message", Channels.appender(ArrayList::new)
        );

        public AppenderState(Map<String, Object> initData) {
            super(initData);
        }

        public Optional<Integer> age() {
            return value("age");
        }

        public Optional<String> lastMessage() {
            var messages = message();
            return ( messages.isEmpty() ) ?
                Optional.empty() :
                Optional.of(messages.getLast());
        }

        List<String> message() {
            return this.<List<String>>value("message")
                .orElseGet(ArrayList::new);
        }

    }

    private Map<String, Object> setsMessage(AppenderState state) {
        return Map.of("message", "Current age " + state.age().get());
    }

    public CompiledGraph<AppenderState> createGraph() throws GraphStateException {
        return new StateGraph<>(AppenderState.SCHEMA, AppenderState::new)
            .addNode("createMessage", node_async(this::setsMessage))
            .addEdge(StateGraph.START, "createMessage")
            .addNode("toUpperCase", node_async(state ->
                Map.of("message", state.lastMessage().get().toUpperCase())))
            .addEdge("createMessage", "toUpperCase")
            .addEdge("toUpperCase", StateGraph.END)
            .compile();
    }

    public static void main(String[] args) throws GraphStateException {
        final AppenderMainApp mainApp = new AppenderMainApp();
        final CompiledGraph<AppenderState> graph = mainApp.createGraph();
        final Optional<AppenderState> finalState = graph.invoke(Map.of("age", 44));
        final AppenderState state = finalState.get();
        System.out.println(state.message());
    }

}
