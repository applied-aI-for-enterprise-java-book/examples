package org.acme.movies;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

@ApplicationScoped
public class MoviesParser {

    @ConfigProperty(name = "movies.file.location.csv")
    File location;

    @Inject
    Logger logger;

    public List<MovieDto> loadMoviesGreaterThanReleaseDate(int releaseYear) {
        try (Reader reader = new FileReader(location);
             CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                     .withFirstRecordAsHeader())) {

            logger.info("Start Parsing Movies");

            List<MovieDto> movieDtos = csvParser
                    .stream()
                    .filter(r -> Integer.parseInt(r.get("Release Year")) > releaseYear)
                    .map(r -> new MovieDto(r.get("Title"), r.get("Director"), r.get("Plot")))
                    .toList();

            logger.info("End Parsing Movies");

            return movieDtos;

        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
