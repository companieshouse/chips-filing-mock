package uk.gov.companieshouse.filingmock.processor;

import java.io.IOException;

import uk.gov.companieshouse.filingmock.model.Address;

public interface Unmarshaller {
    
    /**
     * Function to unmarshall the address from the data string
     * 
     * @param json
     * @return
     * @throws IOException
     */
    Address unmarshallAddress(String json) throws IOException;

}
