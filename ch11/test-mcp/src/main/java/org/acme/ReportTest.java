package org.acme;

import java.nio.file.Path;

public record ReportTest(Path feature, TestResult testResult) {
}
