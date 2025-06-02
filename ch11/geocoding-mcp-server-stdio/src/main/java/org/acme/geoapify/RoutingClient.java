package org.acme.geoapify;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://api.geoapify.com/v1")
public interface RoutingClient {

    record Features(List<Feature> features){}

    record Feature(
         String type,
         Properties properties) {}

    record Leg(int distance, double time, List<Step> steps) {}

    record Properties(
         String mode,
         String units,
         int distance,
         String distance_units,
         double time,
         List<Leg> legs) {}

    record Instruction(String text) {}

    record Step(
        int from_index,
        int to_index,
        int distance,
        double time,
        Instruction instruction){}


    @Path("/routing")
    @GET
    Uni<Features> route(@QueryParam("waypoints") String waypoints,
        @QueryParam("mode") String mode,
        @QueryParam("apiKey") String apiKey);

}
