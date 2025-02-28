package org.acme;


import ai.djl.translate.TranslateException;
import ai.djl.inference.Predictor;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
//import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.concurrent.TimeUnit;

@Path("/distance")
public class VectorsResource {

    /**
     * ❯ curl localhost:8080/distance/cosine
     * Car -> Cat: 0.3818348372562682
     * Car -> Kitten: 0.26044532106396723
     * Cat -> Kitten: 0.8175494710349424
     * ❯ curl localhost:8080/distance/embed
     * Car -> Cat: 0.46332744555413974
     * Car -> Kitten: 0.4349514121523426
     * Cat -> Kitten: 0.7882107954729223
     *
     * Car -> Cat: 0.7552315914270545
     * Car -> Kitten: 0.6926074934435863
     * Cat -> Kitten: 0.8462076949460074
     */

    //EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

    @Inject
    EmbeddingModel embeddingModel;

    @GET
    @Path("/embed")
    @Produces(MediaType.TEXT_PLAIN)
    public String cosineDistance2() throws InterruptedException {
        Response<Embedding> responseCar = embeddingModel.embed("car");
        Response<Embedding> responseCat = embeddingModel.embed("cat");
        TimeUnit.SECONDS.sleep(5);
        Response<Embedding> responseKitten = embeddingModel.embed("kitten");

        StringBuilder distances =
                createStringRepresentationOfDistances(responseCar.content().vector(),
                        responseCat.content().vector(),
                        responseKitten.content().vector());

        return distances.toString();

    }

    @Inject
    Predictor<String, float[]> predict;

    @GET
    @Path("/djl")
    @Produces(MediaType.TEXT_PLAIN)
    public String cosineDistance() throws TranslateException {

        float[] carVector = predict.predict("car");
        float[] catVector = predict.predict("cat");
        float[] kittenVector = predict.predict("kitten");

        StringBuilder distances = createStringRepresentationOfDistances(carVector, catVector, kittenVector);
        return distances.toString();
    }

    private static StringBuilder createStringRepresentationOfDistances(float[] carVector, float[] catVector, float[] kittenVector) {
        StringBuilder distances = new StringBuilder();

        distances.append("Car -> Cat: ");
        distances.append(cosineSimilarity(carVector, catVector));
        distances.append(System.lineSeparator());

        distances.append("Car -> Kitten: ");
        distances.append(cosineSimilarity(carVector, kittenVector));
        distances.append(System.lineSeparator());

        distances.append("Cat -> Kitten: ");
        distances.append(cosineSimilarity(catVector, kittenVector));
        distances.append(System.lineSeparator());
        return distances;
    }

    public static double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}
