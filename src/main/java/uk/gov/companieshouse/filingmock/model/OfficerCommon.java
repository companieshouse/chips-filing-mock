package uk.gov.companieshouse.filingmock.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * An entity model to store common fields among officer filing types TM01, AP01, CH01.
 */
public class OfficerCommon {
    private LocalDate resignedOn;
    private LocalDate appointedOn;
    private LocalDate directorsDetailsChangedDate;

    public OfficerCommon() {
    }

    public OfficerCommon(LocalDate resignedOn, LocalDate appointedOn, LocalDate directorsDetailsChangedDate) {
        this.resignedOn = resignedOn;
        this.appointedOn = appointedOn;
        this.directorsDetailsChangedDate = directorsDetailsChangedDate;
    }

    @JsonProperty("resigned_on")
    public LocalDate getResignedOn() {
        return resignedOn;
    }

    @JsonProperty("appointed_on")
    public LocalDate getAppointedOn() {
        return appointedOn;
    }

    @JsonProperty("directors_details_changed_date")
    public LocalDate getDirectorsDetailsChangedDate() {
        return directorsDetailsChangedDate;
    }
}