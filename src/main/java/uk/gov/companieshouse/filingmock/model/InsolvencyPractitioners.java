package uk.gov.companieshouse.filingmock.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to hold the data for the insolvency practitioners array of objects
 */

public class InsolvencyPractitioners {
    
    @JsonProperty("practitioners")
    private List<InsolvencyPractitioner> practitioners;

    public List<InsolvencyPractitioner> getPractitioners() {
        return practitioners;
    }
}