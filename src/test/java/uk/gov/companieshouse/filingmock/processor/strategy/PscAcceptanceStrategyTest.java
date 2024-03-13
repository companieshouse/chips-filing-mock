package uk.gov.companieshouse.filingmock.processor.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static uk.gov.companieshouse.filingmock.processor.strategy.PscAcceptanceStrategy.INVALID_DATE_ENGLISH_REJECT;
import static uk.gov.companieshouse.filingmock.processor.strategy.PscAcceptanceStrategy.INVALID_DATE_WELSH_REJECT;

class PscAcceptanceStrategyTest {
    private PscAcceptanceStrategy strategy;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        strategy = new PscAcceptanceStrategy();
        transaction = new Transaction();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"ceased_on\":\"2022-08-29\"}", "{}"})
    void acceptCeasedOnDate(String ceasedOn) throws Exception {

        transaction.setData(ceasedOn);
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2022-08-01", "2022-08-16"})
    void rejectCeasedOnDay(LocalDate ceasedOn) throws Exception {
        transaction.setData("{\"ceased_on\":\"" + ceasedOn + "\"}");
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.REJECTED, filingStatus.getStatus());
        assertEquals(1, filingStatus.getRejection().getEnglishReasons().size());
        assertTrue(filingStatus.getRejection().getEnglishReasons().contains(INVALID_DATE_ENGLISH_REJECT));
        assertEquals(1, filingStatus.getRejection().getWelshReasons().size());
        assertTrue(filingStatus.getRejection().getWelshReasons().contains(INVALID_DATE_WELSH_REJECT));
    }

    @Test
    void throwsExceptionInvalidData() {
        transaction.setData("invalid");
        assertThrows(AcceptanceStrategyException.class, () -> strategy.accept(transaction));
    }
}
