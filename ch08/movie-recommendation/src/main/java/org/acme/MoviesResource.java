package org.acme;

import io.quarkus.narayana.jta.runtime.TransactionConfiguration;
import io.quarkus.runtime.Startup;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import me.tongfei.progressbar.ProgressBarStyle;
import org.acme.embedding.EmbeddingCalculator;
import org.acme.movies.MovieDto;
import org.acme.movies.MoviesParser;
import org.acme.tmdb.TmdbService;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.util.List;
import java.util.Random;

@Path("/movies/api")
public class MoviesResource {

    @Inject
    MoviesParser moviesParser;

    @Inject
    EmbeddingCalculator embeddingCalculator;

    @ConfigProperty(name = "embedding.calculate")
    boolean embeddingCalculate;

    @Inject
    Logger logger;

    List<String> highRatings = List.of("Clint Eastwood",
            "Steven Spielberg", "Christopher Nolan", "Hayao Miyazaki");

    Random random = new Random();

    @Startup
    @Transactional
    @TransactionConfiguration(timeout = 500)
    public void startup() {

        // Clint Eastwood, Steven Spielberg, Christopher Nolan

        if (embeddingCalculate) {

            final List<MovieDto> movieDtos = moviesParser
                    .loadMoviesGreaterThanReleaseDate(2007);

            try (ProgressBar pb = new ProgressBarBuilder()
                    .setTaskName("Importing Movies")
                    .setInitialMax(movieDtos.size())
                    .setStyle(ProgressBarStyle.COLORFUL_UNICODE_BAR)
                    .build()) {
                movieDtos.stream()
                        .map(m -> {
                            float[] vector = embeddingCalculator.calculateVector(m);
                            return new Movie(m.title(), m.director(), m.plot(), calculateRating(m), vector);
                        })
                        .forEach(m -> {
                            m.persist();
                            pb.setExtraMessage(m.title);
                            pb.step();
                        });
            }
        } else {
            logger.info("Importing Data from tmpimport.sql file");
        }
    }

    private double calculateRating(MovieDto movieDto) {
        double rating = 0;

        if (highRatings.contains(movieDto.director())) {
            rating = random.nextDouble(4,5);
        }
        else {
            rating = random.nextDouble(0, 5);
        }

        return  Math.round(rating * 10) / 10.0;
    }

    @RestClient
    TmdbService tmdbService;

    @ConfigProperty(name = "tmdb.api.key")
    String tmdbAPiKey;

    public record MovieApiDto(String poster, String name,
                              String plot, String director,
                                double rating){}

    @GET
    @Path("/search")
    public List<MovieApiDto> recommendMovies(@QueryParam("q") String description) {

        float[] plotVector = embeddingCalculator.calculateVector(description);

        List<Movie> movies = Movie.suggestProducts(plotVector, highRatings);
        return movies.stream()
                .map(m -> new MovieApiDto(findPoster(m.title), m.title,
                        cutPlot(m.plot), m.director, m.rating))
                .toList();

    }

    @ConfigProperty(name = "poster.download")
    boolean posterDownload;

    private String findPoster(String title) {
        if (posterDownload) {
            TmdbService.TmdbMovies tmdbMovies = tmdbService.searchMovie(tmdbAPiKey, title);
            return "http://image.tmdb.org/t/p/w500/" + tmdbMovies.results().getFirst().poster_path();
        }

        return "";
    }

    private String cutPlot(String plot) {
        return plot.substring(0, Math.min(plot.length(), 400));
    }
}
