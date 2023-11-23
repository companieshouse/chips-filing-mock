package uk.gov.companieshouse.filingmock.processor.strategy;

import java.io.IOException;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.RegisteredEmailAddress;
import uk.gov.companieshouse.filingmock.model.Status;

/**
 * Rejects the filing if the provided email does not match the Companies House regex (from registered-emil-address-api).
 *
 */
@Component
public class ReaAcceptanceStrategy implements AcceptanceStrategy {

    private static final ObjectReader EMAIL_READER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readerFor(RegisteredEmailAddress.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^.+@.+\\..+$");

    // TODO get the email rejection texts
    private static final String CH_REA_ENGLISH_REJECT = "The postcode you have supplied cannot be Companies House postcode";
    private static final String CH_REA_WELSH_REJECT = "Ni all y cod post rydych wedi'i gyflenwi fod yn god post Tŷ'r Cwmnïau";

    private static RegisteredEmailAddress getRegisteredEmailAddress(Transaction transaction) throws AcceptanceStrategyException {
        try {
            return EMAIL_READER.readValue(transaction.getData());
        } catch (IOException e) {
            throw new AcceptanceStrategyException(e);
        }
    }

    private static boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        return EMAIL_PATTERN.matcher(email).matches();
    }

    @Override
    public FilingStatus accept(Transaction transaction) throws AcceptanceStrategyException {
        FilingStatus filingStatus = new FilingStatus();

        RegisteredEmailAddress rea = getRegisteredEmailAddress(transaction);

        if (!isValidEmail(rea.getRegisteredEmailAddress())) {
            filingStatus.setStatus(Status.REJECTED);
            filingStatus.addRejection(CH_REA_ENGLISH_REJECT, CH_REA_WELSH_REJECT);
        }

        return filingStatus;
    }

}
