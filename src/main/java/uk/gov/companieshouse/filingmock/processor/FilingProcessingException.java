package uk.gov.companieshouse.filingmock.processor;

import uk.gov.companieshouse.filing.received.FilingReceived;

public class FilingProcessingException extends Exception {

    private static final long serialVersionUID = 2081072809279727919L;

    private final FilingReceived filingReceived;

    public FilingProcessingException(FilingReceived filingReceived) {
        super();
        this.filingReceived = filingReceived;
    }

    public FilingProcessingException(String message, FilingReceived filingReceived, Throwable cause) {
        super(message, cause);
        this.filingReceived = filingReceived;
    }

    public FilingProcessingException(String message, FilingReceived filingReceived) {
        super(message);
        this.filingReceived = filingReceived;
    }
    
    public FilingProcessingException(FilingReceived filingReceived, Throwable cause) {
        super(cause);
        this.filingReceived = filingReceived;
    }

    public FilingReceived getFilingReceived() {
        return filingReceived;
    }

}
