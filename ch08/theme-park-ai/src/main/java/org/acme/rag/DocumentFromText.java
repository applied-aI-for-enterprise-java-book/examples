package org.acme.rag;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import jakarta.enterprise.context.ApplicationScoped;

import java.nio.file.Path;
import java.util.List;

@ApplicationScoped
public class DocumentFromText {

    public List<Document> createDocuments(Path directory) {
        return FileSystemDocumentLoader
                .loadDocuments(directory.toAbsolutePath().toString(),
                        new TextDocumentParser());
    }

}
