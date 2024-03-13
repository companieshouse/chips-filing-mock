package uk.gov.companieshouse.filingmock.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

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

// although this wraps only address currently, it could be expanded to cover other fields - hence the generic name
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