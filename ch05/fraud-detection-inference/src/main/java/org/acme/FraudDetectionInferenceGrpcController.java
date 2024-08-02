package org.acme;

import ai.djl.inference.Predictor;
import ai.djl.translate.TranslateException;
import io.grpc.stub.StreamObserver;
import jakarta.annotation.Resource;
import java.util.function.Supplier;
import net.devh.boot.grpc.server.service.GrpcService;
import org.acme.stub.FraudResponse;
import org.acme.stub.TransactionDetails;

@GrpcService
public class FraudDetectionInferenceGrpcController extends org.acme.stub.FraudDetectionGrpc.FraudDetectionImplBase {

    @Resource
    private Supplier<Predictor<org.acme.TransactionDetails, Boolean>> predictorSupplier;

    @Override
    public void predict(TransactionDetails request, StreamObserver<FraudResponse> responseObserver) {
        org.acme.TransactionDetails td =
            new org.acme.TransactionDetails(
                request.getTxUd(),
                request.getDistanceFromLastTransaction(),
                request.getRatioToMedianPrice(),
                request.getUsedChip(),
                request.getUsedPinNumber(),
                request.getOnlineOrder()
            );

        try (var p = predictorSupplier.get()) {

                boolean fraud = p.predict(td);
                FraudResponse fraudResponse = FraudResponse.newBuilder()
                    .setTxUd(td.txId())
                    .setFraud(fraud).build();
                responseObserver.onNext(fraudResponse);
                responseObserver.onCompleted();

        } catch (TranslateException e) {
            throw new RuntimeException(e);
        }
    }

}
