package uk.gov.companieshouse.processor;

import java.io.IOException;

import uk.gov.companieshouse.Address;

/**
 * JsonUnmarshaller class
 *
 * Unmarshalls the json input string into a filing object.
 */

public class JsonUnmarshaller implements Unmarshaller {

    public Address unmarshallAddress(String json) throws IOException {
        return getObjectMapper().readValue(json, Address.class);
    }

}
