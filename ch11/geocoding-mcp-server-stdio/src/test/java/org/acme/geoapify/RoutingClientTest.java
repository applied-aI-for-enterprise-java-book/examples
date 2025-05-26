package org.acme.geoapify;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.assertj.core.api.Assertions;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class RoutingClientTest {

    @ConfigProperty(name = "geoapify.key")
    String apiKey;

    @RestClient
    RoutingClient routingClient;

    @RestClient
    ForwardClient forwardClient;

    /**@Test
    public void shouldConvertAddress() {
        final Uni<ForwardClient.Results>
            resolved = forwardClient.resolve("11 Av. de la Bourdonnais, 75007 Paris, France", "json", apiKey);
        assertThat(resolved.await().indefinitely().results()).hasSize(1);
    }

    @Test
    public void shouldGetRoute() throws IOException {

        final Uni<RoutingClient.Features> route =
            routingClient.route("50.67902320667227,4.569876996843732|50.66170571489684,4.578667041603012",
                "drive", apiKey);

        assertThat(route.await().indefinitely().features()).hasSize(1);

    }**/
}
