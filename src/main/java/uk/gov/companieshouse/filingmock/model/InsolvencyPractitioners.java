package uk.gov.companieshouse.filingmock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.util.List;

/**
 * Class to hold the data for the insolvency practitioners array of objects
 */

public class InsolvencyPractitioners {
    
    @JsonProperty("practitioners")
    private List<PractitionerWrapper> practitioners;


    public String getPractitionerOnePostcode() {
        if (practitioners == null || practitioners.isEmpty()) {
            return null;
        }
        return practitioners.get(0).address.getPostalCode();
    }

}

class PractitionerWrapper {
    // note that existing Address class was used for ROA, but ROA used snake_case in Kafka whereas Insolvency API is using PascalCase
    @JsonProperty("Address")
    IPAddressTruncated address;
}

class IPAddressTruncated {
    @JsonProperty("PostalCode")
    private String postalCode;

    public String getPostalCode() {
        return postalCode;
    }

}