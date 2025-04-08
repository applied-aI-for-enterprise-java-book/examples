package org.acme.ai;

public record EventTime(int year,
                        int month,
                        int day,
                        int hour,
                        int minutes,
                        int duration) {
}
