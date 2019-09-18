package uk.gov.companieshouse.processor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.PresenterRecord;
import uk.gov.companieshouse.filing.processed.ResponseRecord;
import uk.gov.companieshouse.filing.processed.SubmissionRecord;
import uk.gov.companieshouse.filing.received.FilingReceived;

@Component
public class FilingProcessorImpl implements FilingProcessor {

    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";

    @Override
    public FilingProcessed process(FilingReceived filingReceived) {
        FilingProcessed processed = new FilingProcessed();
        processed.setApplicationId(filingReceived.getApplicationId());
        processed.setAttempt(1);
        processed.setChannelId(filingReceived.getChannelId());
        PresenterRecord presenterRecord = new PresenterRecord();
        presenterRecord.setLanguage(filingReceived.getPresenter().getLanguage());
        presenterRecord.setUserId(filingReceived.getPresenter().getUserId());
        processed.setPresenter(presenterRecord);
        processed.setResponse(createResponse(filingReceived));
        SubmissionRecord submissionRecord = new SubmissionRecord();
        submissionRecord.setTransactionId(filingReceived.getSubmission().getTransactionId());
        processed.setSubmission(submissionRecord);
        return processed;
    }

    private ResponseRecord createResponse(FilingReceived filingReceived) {
        ResponseRecord response = new ResponseRecord();
        response.setCompanyName(filingReceived.getSubmission().getCompanyName());
        response.setCompanyNumber(filingReceived.getSubmission().getCompanyNumber());
        response.setDateOfCreation("2019-09-11T13:02:36Z"); // todo now ? this is the format required
        response.setProcessedAt("2019-09-11T13:02:36Z"); // todo now ? this is the format required
        response.setStatus(ACCEPTED);
        response.setSubmissionId(filingReceived.getItems().get(0).getSubmissionId()); // get first item
        return response;
    }

}
