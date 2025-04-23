package org.acme.exchange;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestPath;
import org.jboss.resteasy.reactive.RestQuery;

@RegisterRestClient(configKey = "frankfurt")
@Path("/v1")
public interface ExchangeRateService {

    // {"amount":1.0,"base":"EUR","date":"2025-04-11","rates":{"USD":1.1346}}

    @GET
    @Path("/{currentDate}")
    JsonNode exchange(@RestPath String currentDate,
                      @RestQuery String base,
                      @RestQuery String symbols);
}
