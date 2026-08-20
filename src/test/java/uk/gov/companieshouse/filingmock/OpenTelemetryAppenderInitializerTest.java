package uk.gov.companieshouse.filingmock;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import io.opentelemetry.api.OpenTelemetry;
import org.junit.jupiter.api.Test;

class OpenTelemetryAppenderInitializerTest {

    @Test
    void afterPropertiesSetDoesNotThrow() {
        OpenTelemetryAppenderInitializer initializer =
                new OpenTelemetryAppenderInitializer(OpenTelemetry.noop());

        assertDoesNotThrow(initializer::afterPropertiesSet);
    }
}
