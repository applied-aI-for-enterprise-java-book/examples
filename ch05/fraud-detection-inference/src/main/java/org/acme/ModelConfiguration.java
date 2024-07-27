package org.acme;

import ai.djl.MalformedModelException;
import ai.djl.inference.Predictor;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;

import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import java.io.IOException;
import java.util.function.Supplier;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelConfiguration {

    public static final float THRESHOLD = 0.8f;

    @Bean
    public Criteria<TransactionDetails, Boolean> criteria() {
        String modelLocation = Thread.currentThread()
            .getContextClassLoader()
            .getResource("model.onnx").toExternalForm();

        return Criteria.builder()
            .setTypes(TransactionDetails.class, Boolean.class)
            .optModelUrls(modelLocation)
            .optTranslator(new TransactionTransformer(THRESHOLD))
            .optEngine("OnnxRuntime")
            .optProgress(new ProgressBar())
            .build();
    }

    @Bean
    public ZooModel<TransactionDetails, Boolean> model(
        @Qualifier("criteria") Criteria<TransactionDetails, Boolean> criteria)
        throws MalformedModelException, ModelNotFoundException, IOException {
        return criteria.loadModel();
    }


    @Bean
    public Supplier<Predictor<TransactionDetails, Boolean>> predictorProvider(ZooModel<TransactionDetails, Boolean> model) {
        return model::newPredictor;
    }

}
