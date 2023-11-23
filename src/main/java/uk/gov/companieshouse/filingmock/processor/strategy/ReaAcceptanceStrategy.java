package uk.gov.companieshouse.filingmock.processor.strategy;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.Application;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.RegisteredEmailAddress;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * Rejects the filing if the provided email does not match the Companies House regex (from registered-emil-address-api).
 *
 */
@Component
public class ReaAcceptanceStrategy implements AcceptanceStrategy {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);

    private static final ObjectReader EMAIL_READER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readerFor(RegisteredEmailAddress.class);

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^.+@.+\\..+$");

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
        RegisteredEmailAddress rea = getRegisteredEmailAddress(transaction);

        if (!isValidEmail(rea.getRegisteredEmailAddress())) {
            Map<String, Object> loggedData = new HashMap<>();
            loggedData.put("registeredEmailAddress", rea.getRegisteredEmailAddress());
            LOG.debug("The REA does not match regex but this strategy will not reject the filing", loggedData);
        }

        return new FilingStatus();
    }

}
