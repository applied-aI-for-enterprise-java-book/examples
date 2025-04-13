package org.acme.ai;

import com.fasterxml.jackson.databind.JsonNode;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.exchange.ExchangeRateService;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ExchangeRateTool {

    @RestClient
    ExchangeRateService exchangeRateService;

    @Inject
    Logger logger;

    @Tool("Use this to get exchange rate")
    public JsonNode getExchangeRate(
            @P("The currency to convert from") String currencyFrom,
            @P("The currency to convert to") String currencyTo,
            @P("The date for the exchange rate or \"latest\"") String currencyDate
    ) {

        logger.infof("Getting Exchange Rate %s -> %s at %s", currencyFrom, currencyTo, currencyDate);
       return exchangeRateService.exchange(currencyDate, currencyFrom, currencyTo);
    }


}
