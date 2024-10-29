package uk.gov.companieshouse.filingmock.processor.strategy;

import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;

@FunctionalInterface
public interface AcceptanceStrategy {

    /**
     * Accept or reject a submission
     *
     * @param transaction
     * @return
     * @throws AcceptanceStrategyException
     */
    FilingStatus accept(Transaction transaction) throws AcceptanceStrategyException;
}
