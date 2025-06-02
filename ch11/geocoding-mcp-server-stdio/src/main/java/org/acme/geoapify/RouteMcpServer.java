package org.acme.geoapify;

import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class RouteMcpServer {

    @Inject
    McpResponseFormatter formatter;

    @Inject
    RouteCalculator routeCalculator;

    @Tool(description = "Gets the driving route to go from a location to another location")
    String calculateRoute(
            @ToolArg(description = "starting address location ") String origin,
            @ToolArg(description = "destination address location") String destination) {

        Instructions route = routeCalculator.findRoute(origin, destination);
        return formatter.formatRoutingInstructions(route);
    }

}
