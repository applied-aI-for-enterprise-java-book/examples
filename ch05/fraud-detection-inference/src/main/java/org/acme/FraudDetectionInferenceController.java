package org.acme;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import jakarta.annotation.Resource;
import java.util.function.Supplier;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FraudDetectionInferenceController {

    @Resource
    private Supplier<Predictor<TransactionDetails, Boolean>> predictorSupplier;

    @PostMapping("/inference")
    FraudResponse detectFraud(@RequestBody TransactionDetails transactionDetails) throws TranslateException {
        try (var p = predictorSupplier.get()) {
            boolean fraud = p.predict(transactionDetails);
            return new FraudResponse(transactionDetails.txId(), fraud);
        }
    }
}
