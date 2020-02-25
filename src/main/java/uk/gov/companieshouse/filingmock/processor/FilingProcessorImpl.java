package uk.gov.companieshouse.filingmock.processor;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.Application;
import uk.gov.companieshouse.filingmock.model.Address;
import uk.gov.companieshouse.filingmock.model.FilingProcessed;
import uk.gov.companieshouse.filingmock.util.DateService;
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
    public List<FilingProcessed> process(FilingReceived filingReceived) throws FilingProcessingException {
        Map<String, Object> loggedData = new HashMap<>();
        loggedData.put("transaction id", filingReceived.getSubmission().getTransactionId());
        LOG.trace("Start of filing processing", loggedData);
        List<FilingProcessed> processedList = new ArrayList<>();
        
        for (Transaction transaction : filingReceived.getItems()) {
            FilingProcessed processed = new FilingProcessed();
            processed.setApplicationId(filingReceived.getApplicationId());
            processed.setChannelId(filingReceived.getChannelId());
            processed.setCompanyName(filingReceived.getSubmission().getCompanyName());
            processed.setCompanyNumber(filingReceived.getSubmission().getCompanyNumber());
            processed.setPresenterLanguage(filingReceived.getPresenter().getLanguage());
            processed.setPresenterId(filingReceived.getPresenter().getUserId());
            processed.setTransactionId(filingReceived.getSubmission().getTransactionId());
            processed.setSubmissionId(transaction.getSubmissionId());
            
            try {
                if (isCompaniesHouseAddress(transaction)) {
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
            
            processedList.add(processed);
            
            Map<String, Object> submissionLoggedData = new HashMap<>();
            submissionLoggedData.put("transaction id", processed.getTransactionId());
            submissionLoggedData.put("submission id", processed.getSubmissionId());
            submissionLoggedData.put("status", processed.getStatus());
            LOG.trace("Submission processed successfully", submissionLoggedData);
        }
        
        loggedData.put("total submissions", processedList.size());
        loggedData.put("accepted submission(s)", processedList.stream().filter(p -> ACCEPTED.equals(p.getStatus())).count());
        loggedData.put("rejected submission(s)", processedList.stream().filter(p -> REJECTED.equals(p.getStatus())).count());
        LOG.info("Filing processed successfully", loggedData);
        return processedList;
    }

    private boolean isCompaniesHouseAddress(Transaction transaction) throws IOException {
        // check transaction for type in future development - not always going to be an address
        Address address = unmarshaller.unmarshallAddress(transaction.getData());
        return StringUtils.isNotEmpty(address.getPostalCode())
                && address.getPostalCode().toUpperCase().replaceAll("\\s", "").equals(CH_POSTCODE);
    }

}
