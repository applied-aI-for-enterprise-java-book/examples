package org.acme;

import io.vertx.ext.web.RoutingContext;
import jakarta.inject.Inject;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import java.util.Map;
import java.util.Optional;
import org.acme.graph.GraphProducer;
import org.bsc.langgraph4j.CompiledGraph;
import org.bsc.langgraph4j.RunnableConfig;
import org.bsc.langgraph4j.checkpoint.BaseCheckpointSaver;
import org.bsc.langgraph4j.state.StateSnapshot;

@Path("/chat")
public class BotResource {

    record ChatRequest(String message) {}
    record ChatResponse(String reply) {}

    @Inject
    CompiledGraph<GraphProducer.State> graph;

    @Inject
    BaseCheckpointSaver baseCheckpointSaver;

    @POST
    public ChatResponse chat(ChatRequest chatRequest) throws Exception {

        var runnableConfig =  RunnableConfig.builder()
            .threadId(getId())
            .build();

        final Optional<StateSnapshot<GraphProducer.State>> stateStateSnapshot =
            graph.stateOf(runnableConfig);

        System.out.println(baseCheckpointSaver);
        System.out.println("**" + baseCheckpointSaver.get(runnableConfig));

        // First time, no previous execution
        if (stateStateSnapshot.isEmpty()) {

            final Optional<GraphProducer.State> optionalState =
                graph.invoke(Map.of("question", chatRequest.message()));

            final GraphProducer.State state = optionalState.get();
            String message = state.missingParameter().orElse(state.result());
            System.out.println("++" +  baseCheckpointSaver.get(runnableConfig));
            return new ChatResponse(message);

        } else {

            final GraphProducer.State state = stateStateSnapshot.get().state();
            String originalQuestion = state.question();
            System.out.println("Original Question: " + originalQuestion);
            String newQuestion = originalQuestion + System.lineSeparator() + chatRequest.message();

            var updateConfig = graph.updateState(runnableConfig,
                Map.of("question", newQuestion),
                null);

            final Optional<GraphProducer.State> optionalState =
                graph.invoke(null, updateConfig);

            return new ChatResponse(optionalState.get().result());

        }
    }

    private String getId() {
        return "1234";
    }

}
