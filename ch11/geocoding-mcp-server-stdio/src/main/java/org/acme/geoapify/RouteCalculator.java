package org.acme.geoapify;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class RouteCalculator {

    @ConfigProperty(name = "geoapify.key")
    String apiKey;

    @RestClient
    RoutingClient routingClient;

    @RestClient
    ForwardClient forwardClient;

    @Inject
    Logger logger;

    public Instructions findRoute(String origin, String destination) {

        final Uni<ForwardClient.Results> originWaypoint = forwardClient.resolve(origin, "json", apiKey);
        final Uni<ForwardClient.Results> destinationWaypoint = forwardClient.resolve(destination, "json", apiKey);

        Uni<Instructions> instructionsUni = Uni.combine()
                .all()
                .unis(originWaypoint, destinationWaypoint)
                // Appends the lat/lon of origin and dest to geoapify required format
                .with(this::calculateWaypoints)
                .onItem()
                .transformToUni(waypoints -> routingClient.route(waypoints, "drive", apiKey))
                .onItem()
                .transform(this::getInstructions);

        Instructions instructions = instructionsUni.await().indefinitely();

        logger.info(instructions);
        return instructions;

    }

    private Instructions getInstructions(RoutingClient.Features features) {
        final RoutingClient.Feature feature = features.features().getFirst();
        // For the sake of simplicity, only one route(leg) is supported (https://apidocs.geoapify.com/docs/routing/#api-outputs)
        final RoutingClient.Leg leg = feature.properties().legs().getFirst();
        final List<Instruction> instructions = leg.steps().stream()
            .map(s -> new Instruction(s.instruction().text(), s.distance()))
            .toList();

        return new Instructions(instructions, feature.properties().distance(), feature.properties().distance_units());
    }

    private String calculateWaypoints(ForwardClient.Results o, ForwardClient.Results d) {
        String result =  "%s,%s|%s,%s".formatted(
            getFirstResult(o).lat(),
            getFirstResult(o).lon(),
            getFirstResult(d).lat(),
            getFirstResult(d).lon());

        System.out.println(result);

        return result;
    }

    private ForwardClient.Result getFirstResult(ForwardClient.Results results) {
        return results.results().getFirst();
    }

}
