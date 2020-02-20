package uk.gov.companieshouse.filingmock.processor;

import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filingmock.model.FilingProcessed;

@FunctionalInterface
public interface FilingProcessor {

    /**
     * Transform a FilingReceived to a FilingProcessed
     * 
     * @param filingReceived
     * @return
     * @throws FilingProcessingException
     */
    FilingProcessed process(FilingReceived filingReceived) throws FilingProcessingException;

}
