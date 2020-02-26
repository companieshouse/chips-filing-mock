package uk.gov.companieshouse.filingmock.reader;

import java.util.Collection;

import uk.gov.companieshouse.filing.received.FilingReceived;

public interface FilingReader {

    /**
     * Read a list of FilingReceived from the kafka topic
     * 
     * @return A collection of messages
     */
    Collection<FilingReceived> read();
}
