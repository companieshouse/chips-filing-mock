package uk.gov.companieshouse.filingmock.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The type Filing processed.
 */
@JsonInclude(Include.NON_NULL)
@JsonAutoDetect(getterVisibility = Visibility.NONE)
public class FilingProcessed {

    @JsonProperty(value = "transaction_id")
    private String transactionId;
    @JsonProperty(value = "submission_id")
    private String submissionId;
    @JsonProperty(value = "channel_id")
    private String channelId;
    @JsonProperty(value = "company_number")
    private String companyNumber;
    @JsonProperty(value = "company_name")
    private String companyName;
    @JsonProperty(value = "application_id")
    private String applicationId;
    @JsonProperty(value = "presenter")
    private Presenter presenter;
    @JsonProperty(value = "processed_at")
    private String processedAt;
    @JsonProperty(value = "status")
    private Status status;
    @JsonProperty(value = "rejection")
    private Rejection rejection;

    /**
     * Gets transaction id.
     *
     * @return the transaction id
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets transaction id.
     *
     * @param transactionId the transaction id
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    /**
     * Gets submission id.
     *
     * @return the submission id
     */
    public String getSubmissionId() {
        return submissionId;
    }

    /**
     * Sets submission id.
     *
     * @param submissionId the submission id
     */
    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    /**
     * Gets channel id.
     *
     * @return the channel id
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Sets channel id.
     *
     * @param channelId the channel id
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    /**
     * Gets company number.
     *
     * @return the company number
     */
    public String getCompanyNumber() {
        return companyNumber;
    }

    /**
     * Sets company number.
     *
     * @param companyNumber the company number
     */
    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    /**
     * Gets company name.
     *
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }

    /**
     * Sets company name.
     *
     * @param companyName the company name
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    /**
     * Gets application id.
     *
     * @return the application id
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Sets application id.
     *
     * @param applicationId the application id
     */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    /**
     * Gets presenter id.
     *
     * @return the presenter id
     */
    public String getPresenterId() {
        return presenter != null ? presenter.userId : null;
    }

    /**
     * Sets presenter id.
     *
     * @param presenterId the presenter id
     */
    public void setPresenterId(String presenterId) {
        if (presenter == null) {
            presenter = new Presenter();
        }
        this.presenter.userId = presenterId;
    }

    /**
     * Gets presenter language.
     *
     * @return the presenter language
     */
    public String getPresenterLanguage() {
        return presenter != null ? presenter.language : null;
    }

    /**
     * Sets presenter language.
     *
     * @param presenterLanguage the presenter language
     */
    public void setPresenterLanguage(String presenterLanguage) {
        if (presenter == null) {
            presenter = new Presenter();
        }
        this.presenter.language = presenterLanguage;
    }

    /**
     * Gets processed at.
     *
     * @return the processed at
     */
    public String getProcessedAt() {
        return processedAt;
    }

    /**
     * Sets processed at.
     *
     * @param processedAt the processed at
     */
    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public Status getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(Status status) {
        this.status = status;
    }

    /**
     * Gets rejection.
     *
     * @return the rejection
     */
    public Rejection getRejection() {
        return this.rejection;
    }

    /**
     * Sets rejection.
     *
     * @param rejection the rejection
     */
    public void setRejection(Rejection rejection) {
        this.rejection = rejection;
    }

    /**
     * The type Presenter.
     */
    @JsonInclude(Include.NON_NULL)
    static class Presenter {

        /**
         * The User id.
         */
        @JsonProperty(value = "user_id")
        String userId;

        /**
         * The Language.
         */
        @JsonProperty(value = "language")
        String language;
    }

}
