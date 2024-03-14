package uk.gov.companieshouse.filingmock.processor.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.companieshouse.filingmock.processor.strategy.PscAcceptanceStrategy.INVALID_DATE_ENGLISH_REJECT;
import static uk.gov.companieshouse.filingmock.processor.strategy.PscAcceptanceStrategy.INVALID_DATE_WELSH_REJECT;

class OfficerAcceptanceStrategyTest {
    private OfficerAcceptanceStrategy strategy;

    private Transaction transaction;

    @BeforeEach
    void setUp() {
        strategy = new OfficerAcceptanceStrategy();
        transaction = new Transaction();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{\"resigned_on\":\"2022-08-29\"}", "{\"appointed_on\":\"2022-08-02\"}", "{\"directors_details_changed_date\":\"2022-08-15\"}", "{}"})
    void acceptDates(String ceasedOn) throws Exception {
        transaction.setData(ceasedOn);
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @ParameterizedTest
    @ValueSource(strings = {"2022-08-01", "2022-08-16"})
    void rejectResignedOnDay(LocalDate resignedOn) throws Exception {
        transaction.setData("{\"resigned_on\":\"" + resignedOn + "\"}");
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.REJECTED, filingStatus.getStatus());
        assertEquals(1, filingStatus.getRejection().getEnglishReasons().size());
        assertTrue(filingStatus.getRejection().getEnglishReasons().contains(INVALID_DATE_ENGLISH_REJECT));
        assertEquals(1, filingStatus.getRejection().getWelshReasons().size());
        assertTrue(filingStatus.getRejection().getWelshReasons().contains(INVALID_DATE_WELSH_REJECT));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2022-08-01", "2022-08-16"})
    void rejectAppointedOnDay(LocalDate appointedOn) throws Exception {
        transaction.setData("{\"appointed_on\":\"" + appointedOn + "\"}");
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.REJECTED, filingStatus.getStatus());
        assertEquals(1, filingStatus.getRejection().getEnglishReasons().size());
        assertTrue(filingStatus.getRejection().getEnglishReasons().contains(INVALID_DATE_ENGLISH_REJECT));
        assertEquals(1, filingStatus.getRejection().getWelshReasons().size());
        assertTrue(filingStatus.getRejection().getWelshReasons().contains(INVALID_DATE_WELSH_REJECT));
    }

    @ParameterizedTest
    @ValueSource(strings = {"2022-08-01", "2022-08-16"})
    void rejectDirectorsDetailsChangedDateDay(LocalDate directorsDetailsChangedDate) throws Exception {
        transaction.setData("{\"directors_details_changed_date\":\"" + directorsDetailsChangedDate + "\"}");
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
