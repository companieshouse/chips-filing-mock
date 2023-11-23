package uk.gov.companieshouse.filingmock.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to hold the data for RegisteredEmailAddress objects.
 */
public class RegisteredEmailAddress {

    @JsonProperty("trading_on_market")
    private boolean tradingOnMarket;

    @JsonProperty("dtr5_ind")
    private boolean dtr5Ind;

    @JsonProperty("confirmation_statement_date")
    private String confirmationStatementDate;

    @JsonProperty("registered_email_address")
    private String registeredEmailAddress;

    public boolean isTradingOnMarket() {
        return tradingOnMarket;
    }

    public void setTradingOnMarket(boolean tradingOnMarket) {
        this.tradingOnMarket = tradingOnMarket;
    }

    public boolean isDtr5Ind() {
        return dtr5Ind;
    }

    public void setDtr5Ind(boolean dtr5Ind) {
        this.dtr5Ind = dtr5Ind;
    }

    public String getConfirmationStatementDate() {
        return confirmationStatementDate;
    }

    public void setConfirmationStatementDate(String confirmationStatementDate) {
        this.confirmationStatementDate = confirmationStatementDate;
    }

    public String getRegisteredEmailAddress() {
        return registeredEmailAddress;
    }

    public void setRegisteredEmailAddress(String registeredEmailAddress) {
        this.registeredEmailAddress = registeredEmailAddress;
    }

}
