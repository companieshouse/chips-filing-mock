package uk.gov.companieshouse.filingmock.writer;

import uk.gov.companieshouse.filingmock.model.FilingProcessed;

@FunctionalInterface
public interface FilingWriter {

    /**
     * Write a FilingProcessed to the kafka topic
     *
     * @param filingProcessed The filing processed message
     * @return
     */
    boolean write(FilingProcessed filingProcessed) throws FilingWriterException;
}
