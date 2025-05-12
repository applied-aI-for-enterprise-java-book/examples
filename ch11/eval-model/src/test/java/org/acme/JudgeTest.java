package org.acme;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.scoring.ScoringModel;
import io.quarkiverse.langchain4j.scorer.junit5.AiScorer;
import io.quarkiverse.langchain4j.scorer.junit5.ScorerConfiguration;
import io.quarkiverse.langchain4j.testing.scorer.EvaluationReport;
import io.quarkiverse.langchain4j.testing.scorer.EvaluationStrategy;
import io.quarkiverse.langchain4j.testing.scorer.Parameters;
import io.quarkiverse.langchain4j.testing.scorer.Samples;
import io.quarkiverse.langchain4j.testing.scorer.Scorer;
import io.quarkiverse.langchain4j.testing.scorer.judge.AiJudgeStrategy;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.enterprise.context.control.ActivateRequestContext;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@AiScorer
@QuarkusTest
public class JudgeTest {

    @Inject
    ScoringModel scoringModel;

    @Inject
    Assistant assistant;

    @Inject
    AiServiceEvaluation aiServiceEvaluation;

    @Inject
    ChatModel judge;

    @Test
    void evaluateSamples(@ScorerConfiguration Scorer scorer) {
        final Samples<String> evaluationSamples = CsvLoader.load("src/test/resources/test.csv");
        EvaluationStrategy<String> strategy = new AiJudgeStrategy(judge);

        EvaluationReport<String> report = scorer.evaluate(evaluationSamples, aiServiceEvaluation, strategy);
        assertThat(report.score()).isGreaterThan(80.0);
    }

    @Singleton
    public static class AiServiceEvaluation implements Function<Parameters, String> {

        @Inject
        Assistant assistant;

        @ActivateRequestContext
        @Override
        public String apply(Parameters params) {
            final String input = params.get(0);
            return assistant.assist(input);
        }
    }
}
