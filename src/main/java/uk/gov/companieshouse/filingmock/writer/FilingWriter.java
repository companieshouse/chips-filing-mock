package uk.gov.companieshouse.filingmock.writer;

import uk.gov.companieshouse.filingmock.model.FilingProcessed;

/**
 * The interface Filing writer.
 */
@FunctionalInterface
public interface FilingWriter {

    /**
     * Write a FilingProcessed to the kafka topic.
     *
     * @param filingProcessed The filing processed message
     * @return boolean
     * @throws FilingWriterException the filing writer exception
     */
    boolean write(FilingProcessed filingProcessed) throws FilingWriterException;
}
