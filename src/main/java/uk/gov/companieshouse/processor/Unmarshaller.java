package uk.gov.companieshouse.processor;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.companieshouse.Address;

public interface Unmarshaller {

    /**
     * Default function gets an ObjectMapper instance
     *
     * @return ObjectMapper
     */
    default ObjectMapper getObjectMapper() {
        return new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Function to unmarshall the address from the data string
     * @param json
     * @return
     * @throws IOException
     */
    Address unmarshallAddress(String json) throws IOException;


}
