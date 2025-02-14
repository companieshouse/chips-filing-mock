package uk.gov.companieshouse.filingmock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Class to hold the data for ROA address objects.
 */

public class Address {

    @JsonProperty("address_line_1")
    private String addressLine1;

    @JsonProperty("address_line_2")
    private String addressLine2;

    @JsonProperty("country")
    private String country;

    @JsonProperty("locality")
    private String locality;

    @JsonProperty("po_box")
    private String poBox;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("premises")
    private String premises;

    @JsonProperty("region")
    private String region;

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

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

    public void setPoBox(String poBox) {
        this.poBox = poBox;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPremises() {
        return premises;
    }

    public void setPremises(String premises) {
        this.premises = premises;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

}