package uk.gov.companieshouse.filingmock.processor;

import java.util.Collection;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filingmock.model.FilingProcessed;

/**
 * The interface Filing processor.
 */
@FunctionalInterface
public interface FilingProcessor {

    /**
     * Transform a FilingReceived to a collection of FilingProcessed.
     *
     * @param filingReceived the filing received
     * @return collection
     * @throws FilingProcessingException the filing processing exception
     */
    Collection<FilingProcessed> process(FilingReceived filingReceived)
            throws FilingProcessingException;

}
