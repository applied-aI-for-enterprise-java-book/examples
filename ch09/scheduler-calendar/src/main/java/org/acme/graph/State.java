package org.acme.graph;

import org.bsc.langgraph4j.state.AgentState;

import java.util.List;
import java.util.Map;

public class State extends AgentState {

    static String NONE = "none";

    public State(Map<String, Object> initData) {
        super(initData);
    }

    public String query() {
        return value("query", "");
    }

    public String currentSelection() {
        return value("currentSelection", "");
    }

    public String meetingName() {
        return value("meetingName", "");
    }

    public List<String> currentSchedule() {
        return value("current", List.of());
    }

    List<String> notValid() {
        return value("notvalid", List.of());
    }

    public boolean noValidSchedule() {
        return NONE.equals(currentSelection());
    }

}
