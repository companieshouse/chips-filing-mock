package uk.gov.companieshouse.filingmock.processor.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;

public class AcceptanceStrategyFactoryTest {

    @Test
    public void getRoaStrategy() {
        Transaction submission = new Transaction();
        submission.setKind("registered-office-address");
        AcceptanceStrategy strategy = AcceptanceStrategyFactory.getStrategy(submission);
        assertTrue(strategy instanceof RoaAcceptanceStrategy);
    }
    
    @Test
    public void getDefaultStrategyUnknownType() throws Exception {
        Transaction submission = new Transaction();
        submission.setKind("unknown-transaction-type");
        AcceptanceStrategy strategy = AcceptanceStrategyFactory.getStrategy(submission);
        // Default strategy is always accept
        FilingStatus strategyResponse = strategy.accept(null);
        assertEquals(Status.ACCEPTED, strategyResponse.getStatus());
        assertNull(strategyResponse.getRejection());
    }
    
    @Test
    public void getDefaultStrategyMissingType() throws Exception {
        Transaction submission = new Transaction();
        AcceptanceStrategy strategy = AcceptanceStrategyFactory.getStrategy(submission);
        // Default strategy is always accept
        FilingStatus strategyResponse = strategy.accept(null);
        assertEquals(Status.ACCEPTED, strategyResponse.getStatus());
        assertNull(strategyResponse.getRejection());
    }
}
