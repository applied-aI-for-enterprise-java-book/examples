package org.acme.tmdb;


import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@RegisterRestClient(configKey = "tmdb")
@Path("/3")
public interface TmdbService {

    record TmdbMovies(List<TmbdMovie> results){
        public record TmbdMovie(String poster_path){}
    }

    @Path("/search/movie")
    @GET
    TmdbMovies searchMovie(@QueryParam("api_key") String apiKey,
                     @QueryParam("query") String query);
}
