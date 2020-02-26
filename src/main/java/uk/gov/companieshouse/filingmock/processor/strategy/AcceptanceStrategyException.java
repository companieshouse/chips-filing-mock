package uk.gov.companieshouse.filingmock.processor.strategy;

public class AcceptanceStrategyException extends Exception {

    private static final long serialVersionUID = 8640849575974420295L;

    public AcceptanceStrategyException(String message) {
        super(message);
    }

    public AcceptanceStrategyException(Throwable cause) {
        super(cause);
    }

    public AcceptanceStrategyException(String message, Throwable cause) {
        super(message, cause);
    }
}
