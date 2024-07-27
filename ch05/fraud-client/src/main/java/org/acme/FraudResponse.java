package org.acme;

public record FraudResponse(String txId, boolean fraud) {
}
