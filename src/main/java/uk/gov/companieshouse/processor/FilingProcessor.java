package uk.gov.companieshouse.processor;

import uk.gov.companieshouse.filing.FilingProcessed;
import uk.gov.companieshouse.filing.FilingReceived;

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
