package uk.gov.companieshouse.filingmock.processor.strategy;

import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;

public class AcceptanceStrategyFactory {

    private static final AcceptanceStrategy ROA = new RoaAcceptanceStrategy();
    private static final AcceptanceStrategy ALWAYS_ACCEPT = t -> new FilingStatus();

    private AcceptanceStrategyFactory() {
        // Private constructor
    }

    public static AcceptanceStrategy getStrategy(Transaction submission) {
        if ("registered-office-address".equals(submission.getKind())) {
            return ROA;
        } else {
            return ALWAYS_ACCEPT;
        }
    }

}
