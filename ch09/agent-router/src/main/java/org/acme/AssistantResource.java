package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Map;
import java.util.Optional;
import org.acme.graph.GraphProducer;
import org.bsc.langgraph4j.CompiledGraph;

@Path("/assist")
public class AssistantResource {

    @Inject
    CompiledGraph<GraphProducer.State> graph;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/delorean")
    public String delorean() {
        final Optional<GraphProducer.State> finalState = graph.invoke(
            Map.of("question", "what is the price of a new Flux capacitor for DeLorean car") // <1>
        );

        return finalState.get().generation();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/generic")
    public String generic() {
        final Optional<GraphProducer.State> finalState = graph.invoke(
            Map.of("question", "what is the captial of france") // <1>
        );

        return finalState.get().generation();
    }
}
