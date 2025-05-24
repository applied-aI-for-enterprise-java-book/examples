package org.acme.geoapify;

import io.quarkus.test.junit.QuarkusTest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.assertj.core.api.Assertions;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class RoutingClientTest {

    @RestClient
    RoutingClient routingClient;

    @Test
    public void shouldGetRoute() throws IOException {

        final String apiKey = Files.readString(Paths.get(".env"));

        final RoutingClient.Features route =
            routingClient.route("50.67902320667227,4.569876996843732|50.66170571489684,4.578667041603012",
                "drive", apiKey);

        assertThat(route.features()).hasSize(1);

    }
}
