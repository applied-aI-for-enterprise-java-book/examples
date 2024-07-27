package org.acme;

public record TransactionDetails(String txId, float distanceFromLastTransaction, float ratioToMedianPrice, boolean usedChip,
                                 boolean usedPinNumber, boolean onlineOrder) {}