package org.acme;

import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.scoring.ScoringModel;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class CohereTest {

    @Inject
    ScoringModel scoringModel;

    @Test
    void shouldScoreUsingCohere() {

        System.out.println("Score: " + scoringModel.toString());

        final Response<Double> score =
            scoringModel.score("The capital of France is Paris",
                "What is the capital of France?");

        assertThat(score.content()).isGreaterThan(0.85);


    }

}