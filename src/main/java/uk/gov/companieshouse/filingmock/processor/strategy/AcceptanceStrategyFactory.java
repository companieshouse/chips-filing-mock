package uk.gov.companieshouse.filingmock.processor.strategy;

import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;

public class AcceptanceStrategyFactory {

    private static final AcceptanceStrategy ROA = new RoaAcceptanceStrategy();
    private static final AcceptanceStrategy REA = new ReaAcceptanceStrategy();
    private static final AcceptanceStrategy INSOLVENCY = new InsolvencyAcceptanceStrategy();
    private static final AcceptanceStrategy CESSATION = new PscAcceptanceStrategy();
    private static final AcceptanceStrategy ALWAYS_ACCEPT = t -> new FilingStatus();

    private AcceptanceStrategyFactory() {
        // Private constructor
    }

    public static AcceptanceStrategy getStrategy(Transaction submission) {
        // If getKind() returns null, methods like .contains would generate nullPointer exception, so treat as empty string instead
        String submissionType = (submission.getKind() != null) ? submission.getKind() : "";

        if ("registered-office-address".equals(submissionType)) {
            return ROA;
        } else if ("registered-email-address".equals(submissionType)) {
            return REA;
        } else if (submissionType.startsWith("insolvency")) {
            /* There are multiple insolvency types e.g. insolvency#600 but all will start with "insolvency" and should use the same strategy */
            return INSOLVENCY;
        } else if (submissionType.contains("cessation")) {
            return CESSATION;
        }

        return ALWAYS_ACCEPT;
    }

}
