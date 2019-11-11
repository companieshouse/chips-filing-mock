package uk.gov.companieshouse.filing.processor;

import uk.gov.companieshouse.filing.received.FilingReceived;

public class FilingProcessingException extends Exception {

    private static final long serialVersionUID = 2081072809279727919L;

    private FilingReceived filingReceived;

    public FilingProcessingException(FilingReceived filingReceived) {
        super();
        setFilingReceived(filingReceived);
    }

    public FilingProcessingException(String message, FilingReceived filingReceived, Throwable cause) {
        super(message, cause);
        setFilingReceived(filingReceived);
    }

    public FilingProcessingException(String message, FilingReceived filingReceived) {
        super(message);
        setFilingReceived(filingReceived);
    }
    
    public FilingProcessingException(FilingReceived filingReceived, Throwable cause) {
        super(cause);
        setFilingReceived(filingReceived);
    }
   
    public void setFilingReceived(FilingReceived filingReceived) {
        this.filingReceived = filingReceived;
    }

    public FilingReceived getFilingReceived() {
        return filingReceived;
    }

}
