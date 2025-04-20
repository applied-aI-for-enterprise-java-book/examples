package org.acme.ai;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class InMemoryEmbeddingStoreProducer {

    @Produces
    @ApplicationScoped
    public InMemoryEmbeddingStore<TextSegment> getStore() {
        return new InMemoryEmbeddingStore<>();
    }

}
