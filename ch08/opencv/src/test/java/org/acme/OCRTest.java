package org.acme;

import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

public class OCRTest {


    @Test
    public void readText() {
        final String extractText = OCRProcessor.extractText(Paths.get("src/main/resources/text.png"));
        System.out.println(extractText);
        OCRProcessor.cleanup();
    }

}
