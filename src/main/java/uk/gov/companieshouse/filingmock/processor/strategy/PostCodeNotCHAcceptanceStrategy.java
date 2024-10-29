package uk.gov.companieshouse.filingmock.processor.strategy;

import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;

/**
 * Rejects the filing if it uses any Companies House postcode
 */
abstract class PostCodeNotCHAcceptanceStrategy implements AcceptanceStrategy {

    private static final List<String> CH_POSTCODE = Arrays.asList("CF143UZ", "BT28BG", "SW1H9EX",
            "EH39FF");
    private static final String CH_POSTCODE_ENGLISH_REJECT = "The postcode you have supplied "
            + "cannot be Companies House postcode";
    private static final String CH_POSTCODE_WELSH_REJECT = "Ni all y cod post rydych wedi'i "
            + "gyflenwi fod yn god post Tŷ'r Cwmnïau";

    @Override
    public FilingStatus accept(Transaction transaction) throws AcceptanceStrategyException {
        FilingStatus filingStatus = new FilingStatus();

        if (containsCompaniesHousePostcode(transaction)) {
            filingStatus.setStatus(Status.REJECTED);
            filingStatus.addRejection(CH_POSTCODE_ENGLISH_REJECT, CH_POSTCODE_WELSH_REJECT);
        }

        return filingStatus;
    }

    protected abstract boolean containsCompaniesHousePostcode(Transaction transaction)
            throws AcceptanceStrategyException;

    protected boolean isCHPostCode(String postCode) {
        return !StringUtils.isEmpty(postCode) && CH_POSTCODE.contains(
                postCode.toUpperCase().replaceAll("\\s", ""));
    }

}
