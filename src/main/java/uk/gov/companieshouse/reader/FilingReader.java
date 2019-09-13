package uk.gov.companieshouse.reader;

import java.util.Collection;

import uk.gov.companieshouse.model.FilingReceived;

public interface FilingReader {

    /**
     * Read a list of FilingReceived from the kafka topic
     * 
     * @return A collection of messages
     */
    Collection<FilingReceived> read();
}
