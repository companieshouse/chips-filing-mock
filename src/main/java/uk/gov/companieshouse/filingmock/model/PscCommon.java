package uk.gov.companieshouse.filingmock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

/**
 * An entity model to store common fields among PSC types.
 */
public class PscCommon {

    private LocalDate ceasedOn;

    /**
     * Instantiates a new Psc common.
     */
    public PscCommon() {
    }

    /**
     * Instantiates a new Psc common.
     *
     * @param ceasedOn the ceased on
     */
    public PscCommon(LocalDate ceasedOn) {
        this.ceasedOn = ceasedOn;
    }

    /**
     * Gets ceased on.
     *
     * @return The ceased on date
     */
    @JsonProperty("ceased_on")
    public LocalDate getCeasedOn() {
        return ceasedOn;
    }
}