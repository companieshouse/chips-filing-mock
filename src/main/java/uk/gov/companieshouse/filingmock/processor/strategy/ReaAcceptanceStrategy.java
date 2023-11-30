package uk.gov.companieshouse.filingmock.processor.strategy;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.ConfirmationStatementFilingData;
import uk.gov.companieshouse.filingmock.model.Status;

/**
 * Rejects the filing if the provided email does not match the Companies House regex (from registered-emil-address-api).
 *
 */
@Component
public class ReaAcceptanceStrategy implements AcceptanceStrategy {

    private static final ObjectReader READER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readerFor(ConfirmationStatementFilingData.class);

    private static final String CH_EMAIL = "@companieshouse.gov.uk";

    static final String CH_EMAIL_ENGLISH_REJECT = "The email you have supplied cannot be Companies House email";
    static final String CH_EMAIL_WELSH_REJECT = "Ni all yr e-bost a ddarparwyd gennych fod yn e-bost gan Dŷ'r Cwmnïau";

    private static ConfirmationStatementFilingData getFilingData(Transaction transaction) throws AcceptanceStrategyException {
        try {
            return READER.readValue(transaction.getData());
        } catch (IOException e) {
            throw new AcceptanceStrategyException(e);
        }
    }

    private static boolean isValidEmail(String email) {
        return StringUtils.isEmpty(email) || !email.contains(CH_EMAIL);
    }

    @Override
    public FilingStatus accept(Transaction transaction) throws AcceptanceStrategyException {
        FilingStatus filingStatus = new FilingStatus();

        ConfirmationStatementFilingData data = getFilingData(transaction);

        if (!isValidEmail(data.getRegisteredEmailAddress())) {
            filingStatus.setStatus(Status.REJECTED);
            filingStatus.addRejection(CH_EMAIL_ENGLISH_REJECT, CH_EMAIL_WELSH_REJECT);
        }

        return filingStatus;
    }

}
