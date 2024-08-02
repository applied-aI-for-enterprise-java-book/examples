package org.acme;

import io.quarkus.grpc.GrpcClient;
import io.quarkus.runtime.Startup;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

import java.util.HashMap;
import java.util.Map;

import org.acme.stub.FraudDetection;
import org.acme.stub.FraudRes;
import org.acme.stub.TxDetails;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Path("/fraud")
public class TransactionResource {

    static Map<String, TransactionDetails> txs = new HashMap<>();
    static Map<String, Boolean> fraudTxs = new HashMap<>();

    @Startup
    public void populateData() {
        txs.put("1234", new TransactionDetails("1234",
            0.3111400080477545f,
            1.9459399775518593f,
            true, true, false));

        txs.put("5678", new TransactionDetails("5678",
            0.3111400080477545f,
            1.9459399775518593f,
            true, false, false));
    }

    @RestClient
    FraudDetectionService fraudDetectionService;

    private TransactionDetails findTransactionById(String id) {
        return txs.get(id);
    }

    private void markTransactionFraud(String id, boolean isFraud) {
        fraudTxs.put(id, isFraud);
    }

    private boolean isFraudulent(String id) {
        return fraudTxs.get(id);
    }

    @GET
    @Path("/{txId}")
    public FraudResponse detectFraud(@PathParam("txId") String txId) {
        final TransactionDetails transaction = findTransactionById(txId);

        final FraudResponse fraudResponse = fraudDetectionService.isFraud(transaction);
        markTransactionFraud(fraudResponse.txId(), fraudResponse.fraud());

        return fraudResponse;
    }

    @GrpcClient("fraud")
    FraudDetection fraud;

    @GET
    @Path("/grpc/{txId}")
    public Uni<FraudResponse> detectFraudGrpcClient(@PathParam("txId") String txId) {

        final TransactionDetails tx = findTransactionById(txId);

        final TxDetails txDetails = TxDetails.newBuilder()
            .setTxId(txId)
            .setDistanceFromLastTransaction(tx.distanceFromLastTransaction())
            .setRatioToMedianPrice(tx.ratioToMedianPrice())
            .setOnlineOrder(tx.onlineOrder())
            .setUsedChip(tx.usedChip())
            .setUsedPinNumber(tx.usedPinNumber())
            .build();

        final Uni<FraudRes> predicted = fraud.predict(txDetails);
        return predicted
            .onItem()
            .transform(fr -> new FraudResponse(fr.getTxId(), fr.getFraud()));
    }
}
