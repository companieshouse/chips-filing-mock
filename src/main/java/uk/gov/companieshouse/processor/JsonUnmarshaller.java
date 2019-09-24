package uk.gov.companieshouse.processor;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import uk.gov.companieshouse.Address;

/**
 * JsonUnmarshaller class
 *
 * Unmarshalls the json input string into a filing object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public final class JsonUnmarshaller implements Unmarshaller {

    public Address unmarshallDataStringForAddress(String json) throws IOException {
        return getObjectMapper().readValue(json, Address.class);
    }

}
