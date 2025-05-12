package org.acme;

import io.quarkiverse.langchain4j.testing.scorer.EvaluationSample;
import io.quarkiverse.langchain4j.testing.scorer.Parameter;
import io.quarkiverse.langchain4j.testing.scorer.Parameters;
import io.quarkiverse.langchain4j.testing.scorer.Samples;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CsvLoader {

    private CsvLoader() {

    }

    /**
     * Load samples from a CSV file.
     *
     * @param path the path to the CSV file, must not be {@code null}
     * @return the samples, never {@code null}
     * @param <T> the type of the expected output from the samples.
     */
    @SuppressWarnings("unchecked")
    public static <T> Samples<T> load(String path) {
        if (path == null) {
            throw new IllegalArgumentException("Path must not be null");
        }
        if (path.isBlank()) {
            throw new IllegalArgumentException("Path must not be blank");
        }
        File file = new File(path);
        if (!file.exists()) {
            throw new IllegalArgumentException("File not found: " + path);
        }

        CSVFormat format = CSVFormat.DEFAULT.builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .get();

        List<EvaluationSample<T>> samples = new ArrayList<>();

        try (Reader reader = new FileReader(file);
             CSVParser csvParser = new CSVParser(reader,
                format)) {

            for (CSVRecord record : csvParser) {
                final String name = record.get("name");
                final String input = record.get("input");
                final String output = record.get("output");
                final String tags = record.get("tags");

                List<String> tagsList;

                if (tags == null || tags.isBlank()) {
                    tagsList = List.of();
                } else {
                    tagsList = Arrays.asList(tags.split("\\s*,\\s*"));
                }

                Parameters in = new Parameters();
                if (input == null || input.isBlank()) {
                    throw new RuntimeException("Input not found for sample " + name);
                }

                in.add(new Parameter.UnnamedParameter(input));

                if (output == null || output.isBlank()) {
                    throw new RuntimeException("Output not found for sample " + name);
                }

                samples.add(new EvaluationSample<>(name, in, (T) output, tagsList));

            }

            return new Samples<>(samples);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
