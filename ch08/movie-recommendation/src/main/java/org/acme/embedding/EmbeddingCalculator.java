package org.acme.embedding;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.movies.MovieDto;

@ApplicationScoped
public class EmbeddingCalculator {

    @Inject
    EmbeddingModel embeddingModel;

    public float[] calculateVector(MovieDto movieDto) {
        return calculatVector(movieDto.plot());
    }

    public float[] calculatVector(String text) {
        Response<Embedding> embeddingResponse = embeddingModel.embed(text);
        return embeddingResponse.content().vector();
    }

}
