package uk.gov.companieshouse.filing.processor;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.filing.Application;
import uk.gov.companieshouse.filing.model.Address;
import uk.gov.companieshouse.filing.model.FilingProcessed;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filing.util.DateService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class FilingProcessorImpl implements FilingProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    
    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";
    private static final String CH_POSTCODE = "CF143UZ";
    private static final String CH_POSTCODE_ENGLISH_REJECT = "The postcode you have supplied cannot be Companies House postcode";
    private static final String CH_POSTCODE_WELSH_REJECT = "Ni all y cod post rydych wedi'i gyflenwi fod yn god post Tŷ'r Cwmnïau";

    @Autowired
    private DateService dateService;
    
    @Autowired
    private Unmarshaller unmarshaller;
    
    @Override
    public FilingProcessed process(FilingReceived filingReceived) throws FilingProcessingException {
        LOG.trace("Processing filing for transaction id = " + filingReceived.getSubmission().getTransactionId());
        FilingProcessed processed = new FilingProcessed();
        processed.setApplicationId(filingReceived.getApplicationId());
        processed.setChannelId(filingReceived.getChannelId());
        processed.setCompanyName(filingReceived.getSubmission().getCompanyName());
        processed.setCompanyNumber(filingReceived.getSubmission().getCompanyNumber());
        processed.setPresenterLanguage(filingReceived.getPresenter().getLanguage());
        processed.setPresenterId(filingReceived.getPresenter().getUserId());
        processed.setTransactionId(filingReceived.getSubmission().getTransactionId());
        processed.setSubmissionId(filingReceived.getItems().get(0).getSubmissionId()); // get first item
        
        try {
            if (isCompaniesHouseAddress(filingReceived)) {
                processed.setStatus(REJECTED);
                processed.addRejection(CH_POSTCODE_ENGLISH_REJECT, CH_POSTCODE_WELSH_REJECT);
            } else {
                processed.setStatus(ACCEPTED);
            }
        } catch (IOException e) {
            throw new FilingProcessingException(filingReceived, e);
        }
        String formattedDate = DateTimeFormatter.ISO_INSTANT.format(dateService.now().truncatedTo(ChronoUnit.SECONDS));
        processed.setProcessedAt(formattedDate);
        
        Map<String, Object> loggedData = new HashMap<>();
        loggedData.put("transaction id", filingReceived.getSubmission().getTransactionId());
        loggedData.put("status", processed.getStatus());
        LOG.info("Filing processed successfully", loggedData);
        return processed;
    }

    private boolean isCompaniesHouseAddress(FilingReceived filingReceived) throws IOException {
        Transaction transaction = filingReceived.getItems().get(0);
        // check transaction for type in future development - not always going to be an address
        Address address = unmarshaller.unmarshallAddress(transaction.getData());
        return StringUtils.isNotEmpty(address.getPostalCode())
                && address.getPostalCode().toUpperCase().replaceAll("\\s", "").equals(CH_POSTCODE);
    }

}
