package uk.gov.companieshouse.filingmock.processor.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;

import static org.junit.jupiter.api.Assertions.*;

import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    void acceptValidFirstPostcode() throws Exception {
        transaction.setData(createData(" M8 8EQ ", "CF143UZ"));
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @Test
    void acceptNoPractitioners() throws Exception {
        transaction.setData("{}");
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @Test
    void acceptEmptyPractitioners() throws Exception {
        transaction.setData("{\"practitioners\":[]}");
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @Test
    void acceptFirstPractitionerNoPostcode() throws Exception {
        transaction.setData("{\"practitioners\":[{\"Address\":{}}]}");
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @Test
    void rejectChPostcodeWales() throws Exception {
        postCodeTest("cf14 3UZ", "M8 8EQ");
    }

    @Test
    void rejectChPostcodeEngland() throws Exception {
        postCodeTest("Sw1H   9EX", "AA");
    }

    @Test
    void rejectChPostcodeNorthernIreland() throws Exception {
        postCodeTest("BT2   8bg", "CF144UZ");
    }

    @Test
    void rejectChPostcodeScotland() throws Exception {
        postCodeTest("Eh39fF", "Eh39AF");
    }

    @Test
    void throwsExceptionInvalidData() {
        transaction.setData("invalid");
        assertThrows(AcceptanceStrategyException.class, () -> strategy.accept(transaction));
    }

    private void postCodeTest(String... postCode) throws Exception {
        transaction.setData(createData(postCode));
        FilingStatus filingStatus = strategy.accept(transaction);
        assertEquals(Status.REJECTED, filingStatus.getStatus());
        assertEquals(1, filingStatus.getRejection().getEnglishReasons().size());
        assertTrue(filingStatus.getRejection().getEnglishReasons().contains(ENGLISH_REJECT));
        assertEquals(1, filingStatus.getRejection().getWelshReasons().size());
        assertTrue(filingStatus.getRejection().getWelshReasons().contains(WELSH_REJECT));
        
    }

    private String createData(String... postCodes) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"practitioners\":[");
        sb.append(Stream.of(postCodes).map(p -> "{\"Address\":{\"PostalCode\":\"" + p + "\"}}")
                .collect(Collectors.joining(", ")));
        sb.append("]}");
        return sb.toString();
    }

}
