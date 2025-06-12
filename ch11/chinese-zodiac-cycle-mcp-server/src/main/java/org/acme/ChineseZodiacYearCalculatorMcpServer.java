package org.acme;

import io.quarkiverse.mcp.server.TextContent;
import io.quarkiverse.mcp.server.Tool;
import io.quarkiverse.mcp.server.ToolArg;
import io.quarkiverse.mcp.server.ToolResponse;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.time.DateTimeException;
import java.time.LocalDate;

@Singleton
public class ChineseZodiacYearCalculatorMcpServer {

    @Inject
    ZodiacYearCalculator zodiacYearCalculator;

    @Tool(description = "Gets the Chinese zodiac animal for the given "
        + "date with a format if yyyy-MM-dd")
    public ToolResponse calculatesChineseZodiacAnimalAtDate(

        @ToolArg(name = "localDate", description =
        "The date for which the user wants to know "
            + "the chinese zodiac animal (in yyyy-MM-dd format)")

        String localDate) {

        try {

            LocalDate parsedLocalDate = LocalDate.parse(localDate);
            final String zodiac = zodiacYearCalculator
                .getChineseZodiac(parsedLocalDate.getYear());

            return ToolResponse.success(
                new TextContent(zodiac));

        } catch (DateTimeException e) {
            return ToolResponse.error(
                "Not a valid date (yyyy-MM-dd): " + localDate);
        }
    }


}
