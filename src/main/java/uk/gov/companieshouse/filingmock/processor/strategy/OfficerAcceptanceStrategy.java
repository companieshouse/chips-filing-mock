package uk.gov.companieshouse.filingmock.processor.strategy;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.OfficerCommon;
import uk.gov.companieshouse.filingmock.model.Status;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Rejects the filing if the provided date is the 1st or 16th day of the month.
 * The following dates are supported; resigned_on, appointed_on, directors_details_changed_date.
 */
@Component
public class OfficerAcceptanceStrategy implements AcceptanceStrategy{

    private static final ObjectReader OFFICER_READER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .setDateFormat(new SimpleDateFormat("yyyy-MM-dd"))
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .readerFor(OfficerCommon.class);

    private static final List<Integer> INVALID_DAY_LIST = Arrays.asList(1,16);
    static final String INVALID_DATE_ENGLISH_REJECT = "You can not use the 1st or 16th of a month";
    static final String INVALID_DATE_WELSH_REJECT = "You can not use the 1st or 16th of a month";

    OfficerAcceptanceStrategy() {
        // Package-private constructor
    }

    @Override
    public FilingStatus accept(Transaction transaction) throws AcceptanceStrategyException {

        FilingStatus filingStatus = new FilingStatus();

        OfficerCommon officerCommonData = getOfficerFilingData(transaction);
        if (!isValidDate(officerCommonData)) {
            filingStatus.setStatus(Status.REJECTED);
            filingStatus.addRejection(INVALID_DATE_ENGLISH_REJECT, INVALID_DATE_WELSH_REJECT);
        }

        return filingStatus;
    }

    private OfficerCommon getOfficerFilingData(Transaction transaction) throws AcceptanceStrategyException {
        try {
            return OFFICER_READER.readValue(transaction.getData());
        } catch (IOException e) {
            throw new AcceptanceStrategyException(e);
        }
    }

    private boolean isValidDate(OfficerCommon officerCommonData) {
        return (!INVALID_DAY_LIST.contains(
                Optional.ofNullable(officerCommonData)
                    .map(this::calculateDate)
                    .map(LocalDate::getDayOfMonth)
                    .orElse(0)));
    }

    private LocalDate calculateDate(OfficerCommon officerCommonData) {
        if (officerCommonData.getResignedOn() != null) {
            return officerCommonData.getResignedOn();
        } else if (officerCommonData.getAppointedOn() != null) {
            return officerCommonData.getAppointedOn();
        } else {
            return officerCommonData.getDirectorsDetailsChangedDate();
        }
    }
}
