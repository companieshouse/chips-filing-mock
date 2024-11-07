package uk.gov.companieshouse.filingmock.model;

/**
 * The type Filing status.
 */
public class FilingStatus {

    private Status status = Status.ACCEPTED;
    private Rejection rejection;

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
        return rejection;
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
     * Add rejection.
     *
     * @param englishReason the english reason
     * @param welshReason   the welsh reason
     */
    public void addRejection(String englishReason, String welshReason) {
        if (rejection == null) {
            setRejection(new Rejection());
        }
        rejection.addRejection(englishReason, welshReason);
    }

}
