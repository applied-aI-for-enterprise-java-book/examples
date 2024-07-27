package org.acme;

import io.quarkus.runtime.Startup;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;
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
}
