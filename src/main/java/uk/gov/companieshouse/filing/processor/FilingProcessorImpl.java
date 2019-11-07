package uk.gov.companieshouse.filing.processor;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.filing.model.Address;
import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processed.PresenterRecord;
import uk.gov.companieshouse.filing.processed.RejectRecord;
import uk.gov.companieshouse.filing.processed.ResponseRecord;
import uk.gov.companieshouse.filing.processed.SubmissionRecord;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.Transaction;

@Component
public class FilingProcessorImpl implements FilingProcessor {

    private static final String ACCEPTED = "accepted";
    private static final String REJECTED = "rejected";
    private static final String CH_POSTCODE = "CF143UZ";
    private static final String CH_POSTCODE_ENGLISH_REJECT = "The postcode you have supplied cannot be Companies House postcode";
    private static final String CH_POSTCODE_WELSH_REJECT = "Ni all y cod post rydych wedi'i gyflenwi fod yn god post Tŷ'r Cwmnïau";
    private final SimpleDateFormat sdk = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.UK);
    
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
        String formattedDate = sdk.format(new Date());
        response.setDateOfCreation(formattedDate); // now ?
        response.setProcessedAt(formattedDate); // now ?
        try {
            if (isCompaniesHouseAddress(filingReceived)) {
                response.setStatus(REJECTED);
                response.setReject(createRejectRecord(CH_POSTCODE_ENGLISH_REJECT, CH_POSTCODE_WELSH_REJECT));
            } else {
                response.setStatus(ACCEPTED);
            }
        } catch (IOException e) {
            // JSON address not valid - do nothing
        }

        response.setSubmissionId(filingReceived.getItems().get(0).getSubmissionId()); // get first item
        return response;
    }

    private RejectRecord createRejectRecord(String englishReject, String welshReject) {
        RejectRecord rejectRecord = new RejectRecord();

        List<String> engRejReason = new ArrayList<>();
        engRejReason.add(englishReject);
        rejectRecord.setReasonsEnglish(engRejReason);

        List<String> welshRejReason = new ArrayList<>();
        welshRejReason.add(welshReject);
        rejectRecord.setReasonsWelsh(welshRejReason);
        return rejectRecord;
    }

    private boolean isCompaniesHouseAddress(FilingReceived filingReceived) throws IOException {
        Transaction transaction = filingReceived.getItems().get(0);
        // check transaction for type in future development - not always going to be an address
        Address address = new JsonUnmarshaller().unmarshallAddress(transaction.getData());
        return (StringUtils.isNotEmpty(address.getPostalCode())
                && (address.getPostalCode().toUpperCase().replaceAll("\\s", "").equals(CH_POSTCODE)));
    }

}
