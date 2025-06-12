package org.acme;

import jakarta.inject.Singleton;

@Singleton
public class ZodiacYearCalculator {

    private static final String[] ANIMALS = {
        "Rat", "Ox", "Tiger", "Rabbit", "Dragon", "Snake",
        "Horse", "Goat", "Monkey", "Rooster", "Dog", "Pig"
    };

    /**
     * Returns the Chinese zodiac animal for a given year.
     *
     * @param year the year to check
     * @return the Chinese zodiac animal
     */
    public String getChineseZodiac(int year) {
        int baseYear = 2020; // 2020 is a Rat year
        int index = (year - baseYear) % 12;
        if (index < 0) {
            index += 12; // Adjust for negative years
        }
        return ANIMALS[index];
    }

}
