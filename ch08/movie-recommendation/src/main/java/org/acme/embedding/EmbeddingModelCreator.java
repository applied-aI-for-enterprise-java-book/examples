package org.acme.embedding;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.OnnxEmbeddingModel;
import dev.langchain4j.model.embedding.onnx.PoolingMode;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.nio.file.Paths;

@ApplicationScoped
public class EmbeddingModelCreator {

    @ConfigProperty(name = "embedding.model.path")
    String modelPath;

    @Produces
    public EmbeddingModel create() {
        PoolingMode poolingMode = PoolingMode.MEAN;
        String model = Paths.get(modelPath, "model.onnx").toAbsolutePath().toString();
        String tokenizer = Paths.get(modelPath, "tokenizer.json").toAbsolutePath().toString();
        return new OnnxEmbeddingModel(model, tokenizer, poolingMode);
    }

}
