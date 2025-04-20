package org.acme;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.langchain4j.data.message.AiMessage;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.acme.ai.CurrencyAgent;

@Path("/convert")
public class ConversionResource {

    @Inject
    CurrencyAgent currencyAgent;

    /**@GET
    public String convert() throws JsonProcessingException {
        AiMessage aiMessage = currencyAgent
                .exchangeRate("How much is the exchange rate for 1 USD to INR?");
        return aiMessage.text();
    }

    @GET
    @Path("/no")
    public String convert2() throws JsonProcessingException {
        System.out.println(currencyAgent
            .exchangeRate("How much is the exchange rate for 1 USD?")
            .toolExecutionRequests());
        return "Hello from Quarkus REST";
    }**/
}
