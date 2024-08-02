package org.acme;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import jakarta.annotation.Resource;
import java.util.function.Supplier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
public class FraudDetectionInferenceController {

    @Resource
    private Supplier<Predictor<TransactionDetails, Boolean>> predictorSupplier;

    private final WebClient webClient;

    public FraudDetectionInferenceController() {
        webClient = WebClient.create("http://localhost:8080");
    }

    @PostMapping("/inference")
    FraudResponse detectFraud(@RequestBody TransactionDetails transactionDetails) throws TranslateException {
        try (var p = predictorSupplier.get()) {
            boolean fraud = p.predict(transactionDetails);
            return new FraudResponse(transactionDetails.txId(), fraud);
        }
    }

    @GetMapping("/fraud/{txId}")
    FraudResponse detectFraud(@PathVariable String txId) {

        TransactionDetails transactionDetails = new TransactionDetails(txId, 0.3111400080477545f, 1.9459399775518593f, true, true, false);

        final ResponseEntity<FraudResponse> fraudResponseResponseEntity = webClient.post()
            .uri("/inference")
            .body(Mono.just(transactionDetails), TransactionDetails.class)
            .retrieve()
            .toEntity(FraudResponse.class)
            .block();

        return fraudResponseResponseEntity.getBody();
    }
}
