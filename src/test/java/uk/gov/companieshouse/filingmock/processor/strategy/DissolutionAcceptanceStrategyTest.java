package uk.gov.companieshouse.filingmock.processor.strategy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DissolutionAcceptanceStrategyTest {

    private DissolutionAcceptanceStrategy strategy;

    private Transaction transaction;

    @BeforeEach
    void setup() {
        strategy = new DissolutionAcceptanceStrategy();
        transaction = new Transaction();
    }

    @ParameterizedTest
    @ValueSource(strings = {"{}", "{\"officers\":null}", "{\"officers\":[]}",
            "{\"officers\":[{\"person_name\":{\"surname\":\"Smith\"}}]}",
            "{\"officers\":[{\"on_behalf_name\":null},{\"on_behalf_name\":\"Acme Ltd\"}]}"})
    void accept(String transactionData) throws Exception {
        transaction.setData(transactionData);

        FilingStatus filingStatus = strategy.accept(transaction);

        assertEquals(Status.ACCEPTED, filingStatus.getStatus());
        assertNull(filingStatus.getRejection());
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "{\"officers\":[{\"on_behalf_name\":\"reject\"}]}",
            "{\"officers\":[{\"on_behalf_name\":\"Acme Ltd\"},{\"on_behalf_name\":\"Please REJECT me\"}]}"})
    void reject(String transactionData) throws Exception {
        transaction.setData(transactionData);

        FilingStatus filingStatus = strategy.accept(transaction);

        assertEquals(Status.REJECTED, filingStatus.getStatus());
        assertEquals(1, filingStatus.getRejection().getEnglishReasons().size());
        assertEquals(DissolutionAcceptanceStrategy.ON_BEHALF_NAME_ENGLISH_REJECT,
                filingStatus.getRejection().getEnglishReasons().getFirst());
        assertEquals(DissolutionAcceptanceStrategy.ON_BEHALF_NAME_WELSH_REJECT,
                filingStatus.getRejection().getWelshReasons().getFirst());
    }

    @Test
    void throwsExceptionInvalidData() {
        transaction.setData("invalid");
        assertThrows(AcceptanceStrategyException.class, () -> strategy.accept(transaction));
    }
}
