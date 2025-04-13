package org.acme;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.acme.ai.CurrencyAgent;

@Path("/convert")
public class GreetingResource {

    @Inject
    CurrencyAgent currencyAgent;

    @GET
    public String hello() {
        System.out.println(currencyAgent
                .exchangeRate("How much is the exchange rate for 1 USD to INR?")
                .content().toolExecutionRequests());
        return "Hello from Quarkus REST";
    }
}
