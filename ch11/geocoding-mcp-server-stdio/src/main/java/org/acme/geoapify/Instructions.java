package org.acme.geoapify;

import java.util.List;

public record Instructions(List<Instruction> instructions, int totalDistance, String distanceUnits) {
}
