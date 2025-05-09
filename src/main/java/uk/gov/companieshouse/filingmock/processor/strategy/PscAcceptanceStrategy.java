package uk.gov.companieshouse.filingmock.processor.strategy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.PscCommon;
import uk.gov.companieshouse.filingmock.model.Status;

/**
 * Rejects the filing if the provided psc ceased_on date is the 1st or 16th day of the month.
 */
@Component
public class PscAcceptanceStrategy implements AcceptanceStrategy {

    static final String INVALID_DATE_ENGLISH_REJECT = "You can not use the 1st or 16th of a month";
    static final String INVALID_DATE_WELSH_REJECT = "You can not use the 1st or 16th of a month";
    private static final ObjectReader PSC_READER = new ObjectMapper().registerModule(
                    new JavaTimeModule()).setPropertyNamingStrategy(new SnakeCaseStrategy())
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readerFor(PscCommon.class);
    private static final List<Integer> INVALID_DAY_LIST = Arrays.asList(1, 16);

    PscAcceptanceStrategy() {
        // Private constructor
    }

    @Override
    public FilingStatus accept(Transaction transaction) throws AcceptanceStrategyException {

        FilingStatus filingStatus = new FilingStatus();

        PscCommon pscFilingData = getPscFilingData(transaction);
        if (!isValidCeasedOnDate(pscFilingData)) {
            filingStatus.setStatus(Status.REJECTED);
            filingStatus.addRejection(INVALID_DATE_ENGLISH_REJECT, INVALID_DATE_WELSH_REJECT);
        }

        return filingStatus;
    }

    private PscCommon getPscFilingData(Transaction transaction) throws AcceptanceStrategyException {

        try {
            return PSC_READER.readValue(transaction.getData());
        } catch (IOException ex) {
            throw new AcceptanceStrategyException(ex);
        }
    }

    private boolean isValidCeasedOnDate(PscCommon pscFilingData) {

        return (!INVALID_DAY_LIST.contains(
                Optional.ofNullable(pscFilingData).map(PscCommon::getCeasedOn)
                        .map(LocalDate::getDayOfMonth).orElse(0)));
    }
}
