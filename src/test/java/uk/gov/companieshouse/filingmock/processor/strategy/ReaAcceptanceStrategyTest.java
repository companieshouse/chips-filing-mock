package uk.gov.companieshouse.filingmock.processor.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;

class ReaAcceptanceStrategyTest {

    private ReaAcceptanceStrategy strategy;

    private Transaction transaction;

    @BeforeEach
    void setup() {
        strategy = new ReaAcceptanceStrategy();
        transaction = new Transaction();
    }

    @ParameterizedTest
    @ValueSource(strings = { "{\"registered_email_address\":\"info@acme.com\"}", "{}",
            "{\"registered_email_address\":null}", "{\"registered_email_address\":\"\"}" })
    void acceptValidEmailEmpty(String json) throws Exception {
        // GIVEN
        transaction.setData(json);

        // WHEN
        FilingStatus filingStatus = strategy.accept(transaction);

        // THEN
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @Test
    void rejectInvalidEmail() throws Exception {
        // GIVEN
        transaction.setData("{\"registered_email_address\":\"info@companieshouse.gov.uk\"}");

        // WHEN
        FilingStatus filingStatus = strategy.accept(transaction);

        // THEN
        assertEquals(Status.REJECTED, filingStatus.getStatus());
        assertEquals(1, filingStatus.getRejection().getEnglishReasons().size());
        assertTrue(filingStatus.getRejection().getEnglishReasons().contains(ReaAcceptanceStrategy.CH_EMAIL_ENGLISH_REJECT));
        assertEquals(1, filingStatus.getRejection().getWelshReasons().size());
        assertTrue(filingStatus.getRejection().getWelshReasons().contains(ReaAcceptanceStrategy.CH_EMAIL_WELSH_REJECT));
    }

    @Test
    void throwsExceptionInvalidData() {
        // GIVEN
        transaction.setData("invalid");

        // WHEN & THEN
        assertThrows(AcceptanceStrategyException.class, () -> strategy.accept(transaction));
    }

}
