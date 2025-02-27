package org.acme;

import ai.djl.MalformedModelException;
import ai.djl.huggingface.translator.TextEmbeddingTranslatorFactory;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.Disposes;
import jakarta.enterprise.inject.Produces;

import java.io.IOException;

@ApplicationScoped
public class TextEmbeddingProducer {

    private static String MODEL_URL = "djl://ai.djl.huggingface.pytorch/sentence-transformers/paraphrase-albert-small-v2";

    private ZooModel<String, float[]> zooModel;

    @Startup
    void initializeModel() throws ModelNotFoundException, MalformedModelException, IOException {

        Criteria<String, float[]> criteria = Criteria.builder()
                .setTypes(String.class, float[].class)
                .optModelUrls(MODEL_URL)
                .optEngine("PyTorch")
                .optTranslatorFactory(new TextEmbeddingTranslatorFactory())
                .optProgress(new ProgressBar())
                .build();

        this.zooModel = criteria.loadModel();
    }

    @Produces
    ZooModel<String, float[]> zooModel() {
        return this.zooModel;
    }

    @Produces
    @RequestScoped
    Predictor<String, float[]> predictor(ZooModel<String, float[]> zooModel) {
        System.out.println("New Predictor");
        return zooModel.newPredictor();
    }

    void close(@Disposes Predictor<String, float[]> predictor) {
        System.out.println("Closing Predictor");
        predictor.close();
    }

}
