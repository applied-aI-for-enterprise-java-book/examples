package org.acme.rag;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.rag.content.Content;
import dev.langchain4j.rag.content.DefaultContent;
import dev.langchain4j.rag.content.injector.DefaultContentInjector;

import java.util.List;
import java.util.stream.Collectors;

public class WindowContentInjector extends DefaultContentInjector {

    @Override
    public ChatMessage inject(List<Content> contents, ChatMessage chatMessage) {

        List<Content> fullContent = contents.stream() // <1>
                .map(content -> {

                    String newContent = content
                            .textSegment()
                            .metadata()
                            .getString("window-content-retriever"); // <2>

                    return new DefaultContent(
                            TextSegment.from(newContent),
                                            content.metadata()); // <3>
                })
                .collect(Collectors.toList());

        return super.inject(fullContent, chatMessage); // <4>
    }
}
