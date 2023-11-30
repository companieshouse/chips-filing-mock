package uk.gov.companieshouse.filingmock.processor.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void acceptValidEmail() throws Exception {
        // GIVEN
        transaction.setData("{\"registered_email_address\":\"info@acme.com\"}");

        // WHEN
        FilingStatus filingStatus = strategy.accept(transaction);

        // THEN
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @Test
    void acceptValidEmailMissing() throws Exception {
        // GIVEN
        transaction.setData("{}");

        // WHEN
        FilingStatus filingStatus = strategy.accept(transaction);

        // THEN
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @Test
    void acceptValidEmailNull() throws Exception {
        // GIVEN
        transaction.setData("{\"registered_email_address\":null}");

        // WHEN
        FilingStatus filingStatus = strategy.accept(transaction);

        // THEN
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @Test
    void acceptValidEmailEmpty() throws Exception {
        // GIVEN
        transaction.setData("{\"registered_email_address\":\"\"}");

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
