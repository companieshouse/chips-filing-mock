package uk.gov.companieshouse.filingmock.processor.strategy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.Address;

/**
 * Rejects the filing if the provided address uses Companies House postcode.
 */
@Component
public class RoaAcceptanceStrategy extends PostCodeNotChAcceptanceStrategy {

    private static final ObjectReader ADDRESS_READER = new ObjectMapper().configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readerFor(Address.class);

    RoaAcceptanceStrategy() {
        // Private constructor
    }

    @Override
    protected boolean containsCompaniesHousePostcode(Transaction transaction)
            throws AcceptanceStrategyException {
        Address address = getAddress(transaction);
        return address != null && isChPostCode(address.getPostalCode());
    }

    private Address getAddress(Transaction transaction) throws AcceptanceStrategyException {
        try {
            return ADDRESS_READER.readValue(transaction.getData());
        } catch (IOException ex) {
            throw new AcceptanceStrategyException(ex);
        }
    }

}
