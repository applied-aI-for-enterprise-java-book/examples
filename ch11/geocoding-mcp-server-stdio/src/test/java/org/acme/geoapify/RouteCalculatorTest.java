package org.acme.geoapify;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
public class RouteCalculatorTest {

    @Inject
    RouteCalculator routeCalculator;

    @Test
    public void shouldCalculateRouteBetweenTwoPoints() {
        String org = "Arístides Maillol s/n, 08028, Barcelona, Spain";
        String dest = "Avinguda Diagonal 660, 08034, Barcelona, Spain";

        final Instructions route = routeCalculator.findRoute(org, dest);
        assertThat(route.totalDistance()).isGreaterThan(3000);
    }

}
