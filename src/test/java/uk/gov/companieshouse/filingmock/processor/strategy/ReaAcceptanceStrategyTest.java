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

    // FIXME rejection is not required for the REA strategy
    private static final String ENGLISH_REJECT = "The email address is in an incorrect format. You must use the correct format, like name@example.com";
    private static final String WELSH_REJECT = "Mae'r cyfeiriad e-bost mewn fformat anghywir. Rhaid i chi ddefnyddio'r fformat cywir, fel name@example.com";

    private ReaAcceptanceStrategy strategy;

    private Transaction transaction;

    @BeforeEach
    void setup() {
        strategy = new ReaAcceptanceStrategy();
        transaction = new Transaction();
    }

    @Test
    void acceptValidEmail() throws Exception {
        transaction.setData("{\"registered_email_address\":\"info@acme.com\"}");
        FilingStatus filingStatus = strategy.accept(transaction);

        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @Test
    void rejectNoEmail() throws Exception {
        emailFormatTest(null);
    }

    @Test
    void rejectInvalidEmail() throws Exception {
        emailFormatTest("info.acme.com");
    }

    private void emailFormatTest(String postCode) throws Exception {
        transaction.setData("{\"registered_email_address\":\""+ postCode +"\"}");
        FilingStatus filingStatus = strategy.accept(transaction);

        assertEquals(Status.REJECTED, filingStatus.getStatus());
        assertEquals(1, filingStatus.getRejection().getEnglishReasons().size());
        assertTrue(filingStatus.getRejection().getEnglishReasons().contains(ENGLISH_REJECT));
        assertEquals(1, filingStatus.getRejection().getWelshReasons().size());
        assertTrue(filingStatus.getRejection().getWelshReasons().contains(WELSH_REJECT));
    }

    @Test
    void throwsExceptionInvalidData() {
        transaction.setData("invalid");

        assertThrows(AcceptanceStrategyException.class, () -> strategy.accept(transaction));
    }

}
