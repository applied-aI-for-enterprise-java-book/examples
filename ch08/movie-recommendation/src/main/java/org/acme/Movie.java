package org.acme;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedNativeQuery;
import org.hibernate.annotations.Array;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Entity
@NamedNativeQuery(name = "suggestMovies",
        resultClass = Movie.class,
        query = "SELECT * FROM public.movie WHERE rating > 4.5 AND director IN :directors " +
                "ORDER BY embedded <-> cast(:vector as vector) LIMIT 3;")
public class Movie extends PanacheEntity {

    @Column(length = 512)
    public String title;

    @Column(length = 256)
    public String director;

    @Column(length = 65_535)
    public String plot;

    @Column
    public double rating;

    @Column
    @JdbcTypeCode(SqlTypes.VECTOR)
    @Array(length = 384)
    @JsonIgnore
    public float[] embedded;

    public Movie() {
    }

    public Movie(String title, String director, String plot, double rating, float[] embedded) {
        this.title = title;
        this.director = director;
        this.plot = plot;
        this.rating = rating;
        this.embedded = embedded;
    }

    public static List<Movie> suggestProducts(float[] vector, List<String> favouriteDirectors) {
        return  getEntityManager()
                .createNamedQuery("suggestMovies", Movie.class)
                .setParameter("vector", vector)
                .setParameter("directors", favouriteDirectors).getResultList();
    }

}
