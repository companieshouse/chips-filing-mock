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

public class RoaAcceptanceStrategyTest {

    private static final String ENGLISH_REJECT = "The postcode you have supplied cannot be Companies House postcode";
    private static final String WELSH_REJECT = "Ni all y cod post rydych wedi'i gyflenwi fod yn god post Tŷ'r Cwmnïau";

    private RoaAcceptanceStrategy strategy;

    private Transaction transaction;

    @BeforeEach
    void setup() {
        strategy = new RoaAcceptanceStrategy();
        transaction = new Transaction();
    }

    @Test
    void acceptValidPostcode() throws Exception {
        transaction.setData("{\"postal_code\":\"NR14 3UZ\"}");
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @Test
    void acceptNoPostcode() throws Exception {
        transaction.setData("{}");
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @Test
    void rejectChPostcode() throws Exception {
        transaction.setData("{\"postal_code\":\"cf14  3UZ\"}");
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
