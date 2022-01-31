package uk.gov.companieshouse.filingmock.processor.strategy;

import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;

public class AcceptanceStrategyFactory {

    private static final AcceptanceStrategy ROA = new RoaAcceptanceStrategy();
    private static final AcceptanceStrategy INSOLVENCY = new InsolvencyAcceptanceStrategy();
    private static final AcceptanceStrategy ALWAYS_ACCEPT = t -> new FilingStatus();

    private AcceptanceStrategyFactory() {
        // Private constructor
    }

    public static AcceptanceStrategy getStrategy(Transaction submission) {
        String submissionType = submission.getKind();

        if ("registered-office-address".equals(submissionType)) {
            return ROA;
        } else if ("insolvency".equals(submissionType)) {
            return INSOLVENCY;
        }
        return ALWAYS_ACCEPT;

    }

}
