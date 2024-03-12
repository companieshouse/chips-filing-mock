package uk.gov.companieshouse.filingmock.processor.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;

import static org.junit.jupiter.api.Assertions.*;

class InsolvencyAcceptanceStrategyTest {

    private static final String ENGLISH_REJECT = "The postcode you have supplied cannot be Companies House postcode";
    private static final String WELSH_REJECT = "Ni all y cod post rydych wedi'i gyflenwi fod yn god post Tŷ'r Cwmnïau";

    private InsolvencyAcceptanceStrategy strategy;

    private Transaction transaction;

    @BeforeEach
    void setup() {
        strategy = new InsolvencyAcceptanceStrategy();
        transaction = new Transaction();
    }

    @Test
    void acceptValidPostcode() throws Exception {
        transaction.setData("{\"practitioners\":[{\"Address\":{\"PostalCode\":\" M8 8EQ \"}}]}");
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
    void rejectChPostcodeWales() throws Exception {
        postCodeTest("cf14 3UZ");
    }
    
    @Test
    void rejectChPostcodeEngland() throws Exception {
        postCodeTest("Sw1H   9EX");
    }
    
    @Test
    void rejectChPostcodeNorthernIreland() throws Exception {
        postCodeTest("BT2   8bg");
    }
    
    @Test
    void rejectChPostcodeScotland() throws Exception {
        postCodeTest("Eh39fF");
    }

    private void postCodeTest(String postCode) throws Exception {
        transaction.setData("{\"practitioners\":[{\"Address\":{\"PostalCode\":\"" + postCode + "\"}}]}");
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
