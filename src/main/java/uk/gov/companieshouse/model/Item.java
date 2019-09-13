package uk.gov.companieshouse.model;

public class Item {
    private String data;
    private String kind;
    private String submissionLanguage;
    private String submissionId;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getSubmissionLanguage() {
        return submissionLanguage;
    }

    public void setSubmissionLanguage(String submissionLanguage) {
        this.submissionLanguage = submissionLanguage;
    }

    public String getSubmissionId() {
        return submissionId;
    }

    public void setSubmissionId(String submissionId) {
        this.submissionId = submissionId;
    }
}
