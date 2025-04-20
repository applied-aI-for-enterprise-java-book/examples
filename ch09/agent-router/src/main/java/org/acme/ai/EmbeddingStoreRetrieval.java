package org.acme.ai;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.rag.RetrievalAugmentor;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.function.Supplier;

@ApplicationScoped
public class EmbeddingStoreRetrieval implements Supplier<RetrievalAugmentor> {

    public EmbeddingStoreRetrieval(InMemoryEmbeddingStore<TextSegment> store, EmbeddingModel embeddingModel) {
        EmbeddingStoreContentRetriever contentRetriever = EmbeddingStoreContentRetriever.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(store)
            .maxResults(1)
            .build();
        augmentor = DefaultRetrievalAugmentor
            .builder()
            .contentRetriever(contentRetriever)
            .build();
    }

    private final RetrievalAugmentor augmentor;

    @Override
    public RetrievalAugmentor get() {
        return augmentor;
    }
}