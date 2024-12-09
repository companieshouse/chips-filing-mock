package uk.gov.companieshouse.filingmock.writer;

import java.io.Serial;

public class FilingWriterException extends Exception {

    @Serial
    private static final long serialVersionUID = 5858804506996520287L;

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
