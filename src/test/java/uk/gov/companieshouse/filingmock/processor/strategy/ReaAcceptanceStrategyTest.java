package uk.gov.companieshouse.filingmock.processor.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void throwsExceptionInvalidData() {
        // GIVEN
        transaction.setData("invalid");

        // WHEN & THEN
        assertThrows(AcceptanceStrategyException.class, () -> strategy.accept(transaction));
    }

}
