package org.acme.geoapify;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class RouteCalculatorTest {

    @Inject
    RouteCalculator routeCalculator;

    @Test
    public void shouldCalculateRouteBetweenTwoPoints() {
        String org = "Arístides Maillol s/n, 08028, Barcelona, Spain";
        String dest = "Avinguda Diagonal 660, 08034, Barcelona, Spain";

        final Uni<Instructions> route = routeCalculator.findRoute(org, dest);
        //System.out.println(route.await().indefinitely().instructions());
    }

}
