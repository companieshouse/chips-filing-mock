package uk.gov.companieshouse.writer;

import uk.gov.companieshouse.model.FilingProcessed;

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
