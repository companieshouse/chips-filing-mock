package uk.gov.companieshouse.filingmock.processor;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import uk.gov.companieshouse.filingmock.model.Address;

/**
 * JsonUnmarshaller class
 *
 * Unmarshalls the json input string into a filing object.
 */
@Component
public class JsonUnmarshaller implements Unmarshaller {
    
    private static final ObjectReader ADDRESS_READER = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false).readerFor(Address.class);

    @Override
    public Address unmarshallAddress(String json) throws JsonProcessingException {
        return ADDRESS_READER.readValue(json);
    }

}
