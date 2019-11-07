package uk.gov.companieshouse.filing.writer;

public class FilingWriterException extends Exception {

    private static final long serialVersionUID = 5858804506996520287L;

    public FilingWriterException() {
        super();
    }

    public FilingWriterException(String message, Throwable cause, boolean enableSuppression,
            boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public FilingWriterException(String message, Throwable cause) {
        super(message, cause);
    }

    public FilingWriterException(String message) {
        super(message);
    }

    public FilingWriterException(Throwable cause) {
        super(cause);
    }

}
