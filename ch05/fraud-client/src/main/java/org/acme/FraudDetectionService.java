package org.acme;


import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/inference")
@RegisterRestClient(configKey = "fraud-model")
public interface FraudDetectionService {

    @POST
    FraudResponse isFraud(TransactionDetails transactionDetails);

}
