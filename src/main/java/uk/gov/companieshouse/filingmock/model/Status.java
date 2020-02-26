package uk.gov.companieshouse.filingmock.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    @JsonProperty("accepted")
    ACCEPTED, 
    @JsonProperty("rejected")
    REJECTED;
}