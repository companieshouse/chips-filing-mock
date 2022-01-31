package uk.gov.companieshouse.filingmock.processor.strategy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.Address;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Rejects the filing only if the provided address uses Companies House postcode
 *
 */
@Component
public class InsolvencyAcceptanceStrategy implements AcceptanceStrategy {
    private static final ObjectReader ADDRESS_READER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readerFor(Address.class);

    private static final List<String> CH_POSTCODE = Arrays.asList("CF143UZ","BT28BG","SW1H9EX","EH39FF");
    private static final String CH_POSTCODE_ENGLISH_REJECT = "The postcode you have supplied cannot be Companies House postcode";
    private static final String CH_POSTCODE_WELSH_REJECT = "Ni all y cod post rydych wedi'i gyflenwi fod yn god post Tŷ'r Cwmnïau";

    InsolvencyAcceptanceStrategy() {
        // Private constructor
    }

    @Override
    public FilingStatus accept(Transaction transaction) throws AcceptanceStrategyException {
        FilingStatus filingStatus = new FilingStatus();

        Address address = getAddress(transaction);
        if (!isValidAddress(address)) {
            filingStatus.setStatus(Status.REJECTED);
            filingStatus.addRejection(CH_POSTCODE_ENGLISH_REJECT, CH_POSTCODE_WELSH_REJECT);
        }

        return filingStatus;
    }

    private Address getAddress(Transaction transaction) throws AcceptanceStrategyException {
        try {
            return ADDRESS_READER.readValue(transaction.getData());
        } catch (IOException e) {
            throw new AcceptanceStrategyException(e);
        }
    }

    /**
     * An address is always valid unless it uses Companies House's post code
     * 
     * @param address the address extracted from transaction
     * @return a boolean representing whether address uses CH post code
     */
    private boolean isValidAddress(Address address) {
        return StringUtils.isEmpty(address.getPostalCode())
                || !CH_POSTCODE.contains(address.getPostalCode().toUpperCase().replaceAll("\\s", ""));
    }

}
