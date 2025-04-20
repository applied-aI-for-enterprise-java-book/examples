package org.acme.exchange;

import com.fasterxml.jackson.databind.JsonNode;
import io.quarkus.test.junit.QuarkusTest;
import org.assertj.core.api.Assertions;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class ExchangeRateTest {

    @RestClient
    ExchangeRateService exchangeRate;

    @Test
    public void shouldGetStock() {
        JsonNode rating = exchangeRate.exchange("latest", "EUR", "USD");
        assertThat(rating).isNotNull();
    }

}
