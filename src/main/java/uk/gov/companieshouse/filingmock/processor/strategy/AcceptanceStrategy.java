package uk.gov.companieshouse.filingmock.processor.strategy;

import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;

/**
 * The interface Acceptance strategy.
 */
@FunctionalInterface
public interface AcceptanceStrategy {

    /**
     * Accept or reject a submission.
     *
     * @param transaction the transaction
     * @return filing status
     * @throws AcceptanceStrategyException the acceptance strategy exception
     */
    FilingStatus accept(Transaction transaction) throws AcceptanceStrategyException;
}
