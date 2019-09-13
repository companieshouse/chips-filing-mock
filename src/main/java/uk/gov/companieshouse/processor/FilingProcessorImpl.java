package uk.gov.companieshouse.processor;

import org.springframework.stereotype.Component;

import uk.gov.companieshouse.model.FilingProcessed;
import uk.gov.companieshouse.model.FilingReceived;

@Component
public class FilingProcessorImpl implements FilingProcessor {

    @Override
    public FilingProcessed process(FilingReceived filingReceived) {
        // TODO implement
        System.out.println("Processing " + filingReceived.getApplicationId());
        FilingProcessed processed = new FilingProcessed();
        processed.setApplicationId(filingReceived.getApplicationId() + " completed");
        return processed;
    }

}
