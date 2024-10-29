package uk.gov.companieshouse.filingmock.processor.strategy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;

class AcceptanceStrategyFactoryTest {

    @Test
    void getRoaStrategy() {
        Transaction submission = new Transaction();
        submission.setKind("registered-office-address");
        AcceptanceStrategy strategy = AcceptanceStrategyFactory.getStrategy(submission);
        assertInstanceOf(RoaAcceptanceStrategy.class, strategy);
    }

    @Test
    void getPscStrategy() {
        Transaction submission = new Transaction();
        submission.setKind("cessation");
        AcceptanceStrategy strategy = AcceptanceStrategyFactory.getStrategy(submission);
        assertInstanceOf(PscAcceptanceStrategy.class, strategy);
    }

    @Test
    void get600Strategy() {
        Transaction submission = new Transaction();
        submission.setKind("insolvency#600");
        AcceptanceStrategy strategy = AcceptanceStrategyFactory.getStrategy(submission);
        assertInstanceOf(InsolvencyAcceptanceStrategy.class, strategy);
    }

    @Test
    void getDefaultStrategyUnknownType() throws Exception {
        Transaction submission = new Transaction();
        submission.setKind("unknown-transaction-type");
        AcceptanceStrategy strategy = AcceptanceStrategyFactory.getStrategy(submission);
        // Default strategy is always accept
        FilingStatus strategyResponse = strategy.accept(null);
        assertEquals(Status.ACCEPTED, strategyResponse.getStatus());
        assertNull(strategyResponse.getRejection());
    }

    @Test
    void getDefaultStrategyMissingType() throws Exception {
        Transaction submission = new Transaction();
        AcceptanceStrategy strategy = AcceptanceStrategyFactory.getStrategy(submission);
        // Default strategy is always accept
        FilingStatus strategyResponse = strategy.accept(null);
        assertEquals(Status.ACCEPTED, strategyResponse.getStatus());
        assertNull(strategyResponse.getRejection());
    }
}
