package uk.gov.companieshouse.filing.writer;

import uk.gov.companieshouse.filing.processed.FilingProcessed;

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
