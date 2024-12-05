package uk.gov.companieshouse.filingmock.processor.strategy;

import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;

/**
 * The type Acceptance strategy factory.
 */
public class AcceptanceStrategyFactory {

    private static final AcceptanceStrategy ROA = new RoaAcceptanceStrategy();
    private static final AcceptanceStrategy REA = new ReaAcceptanceStrategy();
    private static final AcceptanceStrategy INSOLVENCY = new InsolvencyAcceptanceStrategy();
    private static final AcceptanceStrategy CESSATION = new PscAcceptanceStrategy();
    private static final AcceptanceStrategy ALWAYS_ACCEPT = t -> new FilingStatus();

    private AcceptanceStrategyFactory() {
        // Private constructor
    }

    /**
     * Gets strategy.
     *
     * @param submission the submission
     * @return the strategy
     */
    public static AcceptanceStrategy getStrategy(Transaction submission) {
        // If getKind() returns null, methods like .contains would generate nullPointer
        // exception, so treat as empty string instead
        String submissionType = (submission.getKind() != null) ? submission.getKind() : "";

        /* There are multiple insolvency types e.g. insolvency#600 but all will start with
            "insolvency" and should use the same strategy */
        return switch (submissionType) {
            case "registered-office-address" -> ROA;
            case "registered-email-address" -> REA;
            case String s when s.startsWith("insolvency") -> INSOLVENCY;
            case String s when s.contains("cessation") -> CESSATION;
            case null, default -> ALWAYS_ACCEPT;
        };

    }

}
