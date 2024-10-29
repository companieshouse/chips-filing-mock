package uk.gov.companieshouse.filingmock.processor;

import java.util.Collection;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filingmock.model.FilingProcessed;

@FunctionalInterface
public interface FilingProcessor {

    /**
     * Transform a FilingReceived to a collection of FilingProcessed
     *
     * @param filingReceived
     * @return
     * @throws FilingProcessingException
     */
    Collection<FilingProcessed> process(FilingReceived filingReceived)
            throws FilingProcessingException;

}
