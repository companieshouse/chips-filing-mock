package uk.gov.companieshouse.filing.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;

import org.junit.jupiter.api.Test;

public class DateServiceImplTest {

    private DateServiceImpl service = new DateServiceImpl();

    @Test
    public void testNow() {
        Instant before = Instant.now();

        Instant result = service.now();

        Instant after = Instant.now();

        assertTrue(result.getEpochSecond() <= after.getEpochSecond());
        assertTrue(result.getEpochSecond() >= before.getEpochSecond());
    }

}
