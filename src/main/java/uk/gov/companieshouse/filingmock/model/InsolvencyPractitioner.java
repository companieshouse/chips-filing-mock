package uk.gov.companieshouse.filingmock.model;

import com.fasterxml.jackson.annotation.JsonProperty;

// although this wraps only address currently, it could be expanded to cover other fields - hence
// the generic name
public class InsolvencyPractitioner {

    // note that existing Address class was used for ROA, but ROA used snake_case in Kafka
    // whereas Insolvency API is using PascalCase
    @JsonProperty("Address")
    IpAddressTruncated address;

    public String getPostalCode() {
        return address.getPostalCode();
    }

    static class IpAddressTruncated {

        @JsonProperty("PostalCode")
        private String postalCode;

        String getPostalCode() {
            return postalCode;
        }
    }
}