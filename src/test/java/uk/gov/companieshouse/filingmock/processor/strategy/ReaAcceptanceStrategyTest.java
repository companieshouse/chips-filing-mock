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
    @ValueSource(strings = {"info@acme.com", "companieshouse@gov.uk", "mail@companieshouse.gov"})
    void acceptValidEmailEmpty(String rea) throws Exception {
        // GIVEN
        transaction.setData("{\"registered_email_address\":\"" + rea + "\"}");

        // WHEN
        FilingStatus filingStatus = strategy.accept(transaction);

        // THEN
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @ValueSource(strings = {"{}", "{\"registered_email_address\":null}", "{\"registered_email_address\":\"\"}"})
    void accept(String transactionData) throws Exception {
        // GIVEN
        transaction.setData(transactionData);

        // WHEN
        FilingStatus filingStatus = strategy.accept(transaction);

        // THEN
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @ParameterizedTest
    @ValueSource(strings = {"info@companieshouse.gov.uk", "any@companiesHOUSE.gov.uk",
            "mail@companieshouse.gov.uk "})
    void rejectInvalidEmail(String rea) throws Exception {
        // GIVEN
        transaction.setData("{\"registered_email_address\":\"" + rea + "\"}");

        // WHEN
        FilingStatus filingStatus = strategy.accept(transaction);

        // THEN
        assertEquals(Status.REJECTED, filingStatus.getStatus());
        assertEquals(1, filingStatus.getRejection().getEnglishReasons().size());
        assertTrue(filingStatus.getRejection().getEnglishReasons()
                .contains(ReaAcceptanceStrategy.CH_EMAIL_ENGLISH_REJECT));
        assertEquals(1, filingStatus.getRejection().getWelshReasons().size());
        assertTrue(filingStatus.getRejection().getWelshReasons()
                .contains(ReaAcceptanceStrategy.CH_EMAIL_WELSH_REJECT));
    }

    @Test
    void throwsExceptionInvalidData() {
        // GIVEN
        transaction.setData("invalid");

        // WHEN & THEN
        assertThrows(AcceptanceStrategyException.class, () -> strategy.accept(transaction));
    }

}
