package org.acme.geoapify;

import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import java.util.List;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(baseUri = "https://api.geoapify.com/v1")
public interface ForwardClient {

    record Results(List<Result> results){}

    record Result(double lon, double lat){}

    @Path("/geocode/search")
    @GET
    Uni<Results> resolve(@QueryParam("text") String text,
        @QueryParam("format") String format,
        @QueryParam("apiKey") String apiKey);

}
