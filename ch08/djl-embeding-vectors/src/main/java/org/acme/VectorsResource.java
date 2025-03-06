package org.acme;


import ai.djl.translate.TranslateException;
import ai.djl.inference.Predictor;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
//import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.PoolingMode;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.output.Response;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import smile.manifold.TSNE;
import smile.plot.swing.Canvas;
import smile.plot.swing.Point;
import smile.plot.swing.ScatterPlot;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
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

    EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

    EmbeddingModel localEmbeddingModel = new OnnxEmbeddingModel(
            "/Users/alexsoto/model/granite-embedding-30m-english/model.onnx",
            "/Users/alexsoto/model/granite-embedding-30m-english/tokenizer.json",
            PoolingMode.MEAN);

    @GET
    @Path("/localembed")
    @Produces(MediaType.TEXT_PLAIN)
    public String cosineDistance3() {
        Response<Embedding> responseCar = localEmbeddingModel.embed("car");
        Response<Embedding> responseCat = localEmbeddingModel.embed("cat");
        //TimeUnit.SECONDS.sleep(5);
        Response<Embedding> responseKitten = localEmbeddingModel.embed("kitten");

        float[] vectorCar = responseCar.content().vector();
        float[] vectorCat = responseCat.content().vector();
        float[] vectorKitten = responseKitten.content().vector();

        StringBuilder distances =
                createStringRepresentationOfDistances(vectorCar,
                        vectorCat, vectorKitten);

        return distances.toString();
    }

    /**@Inject
    EmbeddingModel embeddingModel;*/

    @GET
    @Path("/embed")
    @Produces(MediaType.TEXT_PLAIN)
    public String cosineDistance2() throws InterruptedException, InvocationTargetException {
        Response<Embedding> responseCar = embeddingModel.embed("car");
        Response<Embedding> responseCat = embeddingModel.embed("cat");
        //TimeUnit.SECONDS.sleep(5);
        Response<Embedding> responseKitten = embeddingModel.embed("kitten");

        float[] vectorCar = responseCar.content().vector();
        float[] vectorCat = responseCat.content().vector();
        float[] vectorKitten = responseKitten.content().vector();

        StringBuilder distances =
                createStringRepresentationOfDistances(vectorCar,
                        vectorCat, vectorKitten);

        show(List.of(vectorCar, vectorCat, vectorKitten));

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

    public static void show(List<float[]> points ) throws InterruptedException, InvocationTargetException {
        double[][] pointsToReduce = toDoubleArray(points);

        System.out.println("Before reducing");
        System.out.println(Arrays.deepToString(pointsToReduce));

        TSNE tsne = new TSNE(pointsToReduce, 3);
        double[][] reducedData = tsne.coordinates;

        System.out.println("After reducing");
        System.out.println(Arrays.deepToString(reducedData));

        Canvas canvas = ScatterPlot.of(reducedData).canvas();
        canvas.window();
    }

    private static double[][] toDoubleArray(List<float[]> points) {
        double[][] pointsToReduce = points.stream()
                .map(floatArray -> {
                    double[] doubleArray = new double[floatArray.length];
                    for (int i = 0; i < floatArray.length; i++) {
                        doubleArray[i] = (double) floatArray[i];
                    }
                    return doubleArray;
                })
                .toArray(double[][]::new);
        return pointsToReduce;
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
