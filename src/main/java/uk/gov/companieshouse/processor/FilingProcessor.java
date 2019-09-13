package uk.gov.companieshouse.processor;

import uk.gov.companieshouse.model.FilingProcessed;
import uk.gov.companieshouse.model.FilingReceived;

@FunctionalInterface
public interface FilingProcessor {

    /**
     * Transform a FilingReceived to a FilingProcessed
     * 
     * @param filingReceived
     * @return
     */
    FilingProcessed process(FilingReceived filingReceived);

}
