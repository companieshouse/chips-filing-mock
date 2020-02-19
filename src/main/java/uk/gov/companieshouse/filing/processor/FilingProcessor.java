package uk.gov.companieshouse.filing.processor;

import uk.gov.companieshouse.filing.model.FilingProcessed;
import uk.gov.companieshouse.filing.received.FilingReceived;

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
