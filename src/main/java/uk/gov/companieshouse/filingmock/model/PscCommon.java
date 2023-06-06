package uk.gov.companieshouse.filingmock.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * An entity model to store common fields among PSC types.
 */
public class PscCommon {
    private LocalDate ceasedOn;

    public PscCommon() {
    }

    public PscCommon(LocalDate ceasedOn) {
        this.ceasedOn = ceasedOn;
    }

    /**
     * @return The ceased on date
     */
    @JsonProperty("ceased_on")
    public LocalDate getCeasedOn() {
        return ceasedOn;
    }
}