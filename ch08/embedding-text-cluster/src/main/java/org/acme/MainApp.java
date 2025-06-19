package org.acme;


import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiChatModelName;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import org.apache.commons.math3.ml.clustering.Cluster;
import org.apache.commons.math3.ml.clustering.Clusterable;
import org.apache.commons.math3.ml.clustering.DBSCANClusterer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class MainApp {
    public record News(String title) {
    }

    private static final String regex = "\"([^\"]*)\"";

    public static List<News> readNews(Path file) {

        List<News> news = new ArrayList<>();

        String content = null;
        try {
            content = Files.readString(file);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }

        // Compile the pattern
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String quotedString = matcher.group();
            news.add(new News(quotedString));
        }

        return news;
    }

    private static final List<String> newsFiles = List.of("src/main/resources/news-titles.txt", "src/main/resources/news2-titles.txt");

    public static List<News> readNews() {
        return newsFiles.stream()
                .map(Paths::get)
                .map(MainApp::readNews)
                .flatMap(List::stream)
                .toList();
    }

    public record ClusterableEmbeddedMessage(News news, double[] embedding) implements Clusterable {
        @Override
        public double[] getPoint() {
            return embedding;
        }
    }

    public static List<ClusterableEmbeddedMessage> calculate(List<News> newsList) {

        List<ClusterableEmbeddedMessage> clusterableEmbeddedMessageList = new ArrayList<>();
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        for(News news : newsList) {

            final Embedding content = embeddingModel.embed(news.title).content();
            ClusterableEmbeddedMessage clusterableEmbeddedMessage =
                    new ClusterableEmbeddedMessage(news,
                            content.vectorAsList().stream()
                                    .mapToDouble(Float::doubleValue)
                                    .toArray()
                    );

            clusterableEmbeddedMessageList.add(clusterableEmbeddedMessage);
        }

        return clusterableEmbeddedMessageList;
    }

    public interface SummarizerService {

        @SystemMessage("""
         Summarize the following list of news headlines in one simple description.
         Don't give a full sentence saying the headlines are about a topic,
         just give the topic directly in 7 words or less,
         without mentioning the messages are news, be concise .
        """)
        String summarize(String appendedMessages);

    }

    public static String createSummarize(List<? extends Cluster<ClusterableEmbeddedMessage>> clusters) {

        StringBuilder dataTemplate = new StringBuilder();

        ChatModel model = OpenAiChatModel.builder()
                .apiKey("demo")
                .modelName(OpenAiChatModelName.GPT_4_O_MINI)
                .build();

        SummarizerService summarizerService = AiServices.create(SummarizerService.class, model);

        for (final Cluster<ClusterableEmbeddedMessage> cluster : clusters) {
            final List<ClusterableEmbeddedMessage> clusterPoints = cluster.getPoints();

            String appendedTitles = clusterPoints.stream()
                    .map(c -> c.news().title())
                    .collect(Collectors.joining("\n"));


            String clusterSummary = summarizerService.summarize(appendedTitles);

            dataTemplate
                    .append("{name: \"")
                    .append(clusterSummary
                            .replace("\"", "\\\"")
                            .replace("\n", " "))
                    .append("\", value: ")
                    .append(clusterPoints.size())
                    .append("},\n    ");

        }

        return dataTemplate.toString().trim();

    }

    private static final double MAXIMUM_NEIGHBORHOOD_RADIUS = 0.9;
    private static final int MINIMUM_POINTS_PER_CLUSTER = 6;

    public static void main(String[] args) {
        DBSCANClusterer<ClusterableEmbeddedMessage> clusterer = new DBSCANClusterer<>(
                MAXIMUM_NEIGHBORHOOD_RADIUS, MINIMUM_POINTS_PER_CLUSTER);

        final List<ClusterableEmbeddedMessage> points = calculate(readNews());
        final List<? extends Cluster<ClusterableEmbeddedMessage>> clusters = clusterer.cluster(points);
        String summary = createSummarize(clusters);

        System.out.println(summary);

    }
}
