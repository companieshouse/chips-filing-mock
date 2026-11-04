package uk.gov.companieshouse.filingmock.processor.strategy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang3.Strings;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.DissolutionFilingData;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;

import java.io.IOException;
import java.util.Objects;

/**
 * Rejects the dissolution filing if any officer's on behalf of name contains "reject".
 */
@Component
public class DissolutionAcceptanceStrategy implements AcceptanceStrategy {

    static final String ON_BEHALF_NAME_ENGLISH_REJECT =
            "The on behalf of name you have supplied cannot contain 'reject'.";
    static final String ON_BEHALF_NAME_WELSH_REJECT = "Ni all yr enw ar ran a ddarparwyd gennych gynnwys 'reject'.";
    private static final String REJECT_TRIGGER = "reject";
    private static final ObjectReader READER = new ObjectMapper().configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readerFor(DissolutionFilingData.class);

    /**
     * Instantiates a new Dissolution acceptance strategy.
     */
    DissolutionAcceptanceStrategy() {
    }

    @Override
    public FilingStatus accept(Transaction transaction) throws AcceptanceStrategyException {
        FilingStatus filingStatus = new FilingStatus();

        if (hasRejectedOnBehalfName(getFilingData(transaction))) {
            filingStatus.setStatus(Status.REJECTED);
            filingStatus.addRejection(ON_BEHALF_NAME_ENGLISH_REJECT, ON_BEHALF_NAME_WELSH_REJECT);
        }

        return filingStatus;
    }

    private static DissolutionFilingData getFilingData(Transaction transaction) throws AcceptanceStrategyException {
        try {
            return READER.readValue(transaction.getData());
        } catch (IOException ex) {
            throw new AcceptanceStrategyException(ex);
        }
    }

    private static boolean hasRejectedOnBehalfName(DissolutionFilingData data) {
        if (data == null || data.getOfficers() == null) {
            return false;
        }

        return data.getOfficers().stream()
                .filter(Objects::nonNull)
                .map(DissolutionFilingData.Officer::getOnBehalfName)
                .anyMatch(name -> Strings.CI.contains(name, REJECT_TRIGGER));
    }
}
