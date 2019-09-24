package uk.gov.companieshouse;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to hold the data for the address objects
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class Address {
    public String addressLine1;
    public String addressLine2;
    public String country;
    public String locality;
    public String poBox;
    public String postalCode;
    public String premises;
    public String region;

    public Address() {
    }
    
    @JsonCreator
    public Address(@JsonProperty("address_line_1") String addressLine1,
                   @JsonProperty("address_line_2") String addressLine2,
                   @JsonProperty("country") String country,
                   @JsonProperty("locality") String locality,
                   @JsonProperty("po_box") String poBox,
                   @JsonProperty("postal_code") String postalCode,
                   @JsonProperty("premises") String premises,
                   @JsonProperty("region") String region) {
        this.addressLine1 = addressLine1;
        this.addressLine2= addressLine2;
        this.country = country;
        this.locality = locality;
        this.poBox = poBox;
        this.postalCode = postalCode;
        this.premises = premises;
        this.region = region;
    }
}