package org.acme.geoapify;

import io.quarkus.qute.Qute;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Map;
import java.util.stream.Collectors;

@ApplicationScoped
public class McpResponseFormatter {

    public String formatRoutingInstructions(Instructions instructions) {
        String textInstructions = instructions.instructions().stream().map( i -> Qute.fmt(
                """
                       Street: {instruction.text} for {instruction.distance} {unit}
                 """, Map.of("unit", instructions.distanceUnits(),
                        "instruction", i)
        )).collect(Collectors.joining("\n---\n"));

        return "Total Distance: " +
                instructions.totalDistance() +
                " " +
                instructions.distanceUnits() +
                "\n\n" +
                textInstructions;
    }

}
