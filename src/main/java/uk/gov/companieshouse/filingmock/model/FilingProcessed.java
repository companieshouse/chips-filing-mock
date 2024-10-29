package uk.gov.companieshouse.filingmock.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

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

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getPresenterId() {
        return presenter != null ? presenter.userId : null;
    }

    public void setPresenterId(String presenterId) {
        if (presenter == null) {
            presenter = new Presenter();
        }
        this.presenter.userId = presenterId;
    }

    public String getPresenterLanguage() {
        return presenter != null ? presenter.language : null;
    }

    public void setPresenterLanguage(String presenterLanguage) {
        if (presenter == null) {
            presenter = new Presenter();
        }
        this.presenter.language = presenterLanguage;
    }

    public String getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(String processedAt) {
        this.processedAt = processedAt;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Rejection getRejection() {
        return this.rejection;
    }

    public void setRejection(Rejection rejection) {
        this.rejection = rejection;
    }

    @JsonInclude(Include.NON_NULL)
    static class Presenter {

        @JsonProperty(value = "user_id")
        String userId;

        @JsonProperty(value = "language")
        String language;
    }

}
