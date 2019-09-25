package uk.gov.companieshouse;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Class to hold the data for the address objects
 */
@JsonIgnoreProperties(ignoreUnknown = true)

public class Address {
    private String addressLine1;
    private String addressLine2;
    private String country;
    private String locality;
    private String poBox;
    private String postalCode;
    private String premises;
    private String region;

    public Address() {
    }
    
    public String getAddressLine1() {
        return addressLine1;
    }
    
    @JsonSetter("address_line_1")
    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    @JsonSetter("address_line_2")
    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCountry() {
        return country;
    }
    
    @JsonSetter
    public void setCountry(String country) {
        this.country = country;
    }

    public String getLocality() {
        return locality;
    }

    @JsonSetter
    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getPoBox() {
        return poBox;
    }

    @JsonSetter("po_box")
    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public String getPostalCode() {
        return postalCode;
    }

    @JsonSetter("postal_code")
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPremises() {
        return premises;
    }

    @JsonSetter
    public void setPremises(String premises) {
        this.premises = premises;
    }

    public String getRegion() {
        return region;
    }

    @JsonSetter
    public void setRegion(String region) {
        this.region = region;
    }

}