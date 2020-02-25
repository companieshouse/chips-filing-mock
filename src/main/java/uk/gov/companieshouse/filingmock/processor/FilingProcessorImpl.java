package uk.gov.companieshouse.filingmock.processor;

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.Application;
import uk.gov.companieshouse.filingmock.model.FilingProcessed;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;
import uk.gov.companieshouse.filingmock.processor.strategy.AcceptanceStrategy;
import uk.gov.companieshouse.filingmock.processor.strategy.AcceptanceStrategyException;
import uk.gov.companieshouse.filingmock.processor.strategy.AcceptanceStrategyFactory;
import uk.gov.companieshouse.filingmock.util.DateService;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class FilingProcessorImpl implements FilingProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    @Autowired
    private DateService dateService;
    
    @Override
    public List<FilingProcessed> process(FilingReceived filingReceived) throws FilingProcessingException {
        Map<String, Object> loggedData = new HashMap<>();
        loggedData.put("transaction id", filingReceived.getSubmission().getTransactionId());
        LOG.trace("Start of filing processing", loggedData);
        List<FilingProcessed> processedList = new ArrayList<>();
        
        final String processedTime = DateTimeFormatter.ISO_INSTANT.format(dateService.now().truncatedTo(ChronoUnit.SECONDS));
        
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
                FilingStatus status = getStrategy(transaction).accept(transaction);
                processed.setStatus(status.getStatus());
                processed.setRejection(status.getRejection());
            } catch (AcceptanceStrategyException e) {
                throw new FilingProcessingException(filingReceived, e);
            }
            processed.setProcessedAt(processedTime);
            
            processedList.add(processed);
            
            Map<String, Object> submissionLoggedData = new HashMap<>();
            submissionLoggedData.put("transaction id", processed.getTransactionId());
            submissionLoggedData.put("submission id", processed.getSubmissionId());
            submissionLoggedData.put("status", processed.getStatus());
            LOG.trace("Submission processed successfully", submissionLoggedData);
        }
        
        loggedData.put("total submissions", processedList.size());
        loggedData.put("accepted submission(s)", processedList.stream().filter(p -> Status.ACCEPTED.equals(p.getStatus())).count());
        loggedData.put("rejected submission(s)", processedList.stream().filter(p -> Status.REJECTED.equals(p.getStatus())).count());
        LOG.info("Filing processed successfully", loggedData);
        return processedList;
    }

    AcceptanceStrategy getStrategy(Transaction transaction) {
        return AcceptanceStrategyFactory.getStrategy(transaction);
    }
}
