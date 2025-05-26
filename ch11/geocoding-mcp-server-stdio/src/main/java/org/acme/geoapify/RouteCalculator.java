package org.acme.geoapify;

import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class RouteCalculator {

    @ConfigProperty(name = "geoapify.key")
    String apiKey;

    @RestClient
    RoutingClient routingClient;

    @RestClient
    ForwardClient forwardClient;

    public Uni<Instructions> findRoute(String origin, String destination) {

        ForwardClient.Results originWaypoint = forwardClient.resolve(origin, "json", apiKey);
        ForwardClient.Results destinationWaypoint = forwardClient.resolve(destination, "json", apiKey);

        System.out.println("****");
        System.out.println(originWaypoint.results().getFirst().lat());
        System.out.println(destinationWaypoint.results().getFirst().lat());
        return null;
        /**final Uni<ForwardClient.Results> originWaypoint = forwardClient.resolve(origin, "json", apiKey);
        final Uni<ForwardClient.Results> destinationWaypoint = forwardClient.resolve(destination, "json", apiKey);

        return Uni.combine()
            .all()
            .unis(originWaypoint, destinationWaypoint)
            // Appends the lat/lon of origin and dest to geoapify required format
            .with(this::calculateWaypoints)
            .onItem()
            .transformToUni(waypoints -> routingClient.route(waypoints, "drive", apiKey))
            .onItem()
            .transform(this::getInstructions);**/

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
