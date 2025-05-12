package org.acme;

import dev.langchain4j.model.scoring.ScoringModel;
import io.quarkiverse.langchain4j.scorer.junit5.AiScorer;
import io.quarkiverse.langchain4j.scorer.junit5.ScorerConfiguration;
import io.quarkiverse.langchain4j.scorer.junit5.ScorerExtension;
import io.quarkiverse.langchain4j.testing.scorer.EvaluationReport;
import io.quarkiverse.langchain4j.testing.scorer.EvaluationSample;
import io.quarkiverse.langchain4j.testing.scorer.EvaluationStrategy;
import io.quarkiverse.langchain4j.testing.scorer.Samples;
import io.quarkiverse.langchain4j.testing.scorer.Scorer;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(ScorerExtension.class)
@QuarkusTest
public class ReRankerTest {

    @Inject
    ScoringModel scoringModel;

    @Inject
    Assistant assistant;

    @ScorerConfiguration
    private Scorer scorer;

    public class RerankEvaluationStrategy implements EvaluationStrategy<String> {

        private final ScoringModel scoringModel;

        public RerankEvaluationStrategy(ScoringModel scoringModel) {
            this.scoringModel = scoringModel;
        }

        @Override
        public boolean evaluate(EvaluationSample<String> sample, String output) {
            final String input = sample.parameters().get(0);
            return scoringModel.score(output, input).content() > 0.85;
        }
    }

    @Test
    void evaluateSamples() {
        final Samples<String> evaluationSamples = CsvLoader.load("src/test/resources/test.csv");
        EvaluationStrategy<String> strategy = new RerankEvaluationStrategy(scoringModel);

        EvaluationReport<String> report = scorer.evaluate(evaluationSamples, parameters -> {
            final String input = parameters.get(0);
            return assistant.assist(input);
        }, strategy);

        assertThat(report.score()).isGreaterThan(80.0);
    }

}
