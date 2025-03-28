package org.acme;

public record TestResult(Result result, String explanation) {

    public enum Result {
        PASS, FAIL
    }

}
