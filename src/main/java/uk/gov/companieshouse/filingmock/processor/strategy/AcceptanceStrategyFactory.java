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

        switch(submissionType) {
            case "registered-office-address":
                return ROA;
            case "insolvency" :
                return INSOLVENCY;
            default :
                return ALWAYS_ACCEPT;
        }

    }

}
