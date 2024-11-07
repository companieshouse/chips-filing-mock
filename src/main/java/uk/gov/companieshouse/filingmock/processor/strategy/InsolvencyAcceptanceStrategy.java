package uk.gov.companieshouse.filingmock.processor.strategy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import java.io.IOException;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.InsolvencyPractitioners;

/**
 * Rejects the filing only if the first practitioner's address uses Companies House postcode.
 */
@Component
public class InsolvencyAcceptanceStrategy extends PostCodeNotChAcceptanceStrategy {

    private static final ObjectReader PRACTITIONERS_READER = new ObjectMapper().configure(
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readerFor(InsolvencyPractitioners.class);

    /**
     * Instantiates a new Insolvency acceptance strategy.
     */
    InsolvencyAcceptanceStrategy() {
        // Private constructor
    }

    @Override
    protected boolean containsCompaniesHousePostcode(Transaction transaction)
            throws AcceptanceStrategyException {
        InsolvencyPractitioners practitioners = getPractitioners(transaction);
        String postCode = null;
        if (practitioners != null && practitioners.getPractitioners() != null
                && !practitioners.getPractitioners().isEmpty()) {
            postCode = practitioners.getPractitioners().get(0).getPostalCode();
        }
        return isChPostCode(postCode);
    }

    private InsolvencyPractitioners getPractitioners(Transaction transaction)
            throws AcceptanceStrategyException {
        try {
            return PRACTITIONERS_READER.readValue(transaction.getData());
        } catch (IOException ex) {
            throw new AcceptanceStrategyException(ex);
        }
    }
}
