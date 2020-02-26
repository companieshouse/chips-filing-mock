package uk.gov.companieshouse.filingmock.model;

public class FilingStatus {
    private Status status = Status.ACCEPTED;
    private Rejection rejection;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Rejection getRejection() {
        return rejection;
    }

    public void setRejection(Rejection rejection) {
        this.rejection = rejection;
    }

    public void addRejection(String englishReason, String welshReason) {
        if (rejection == null) {
            setRejection(new Rejection());
        }
        rejection.addRejection(englishReason, welshReason);
    }

}
