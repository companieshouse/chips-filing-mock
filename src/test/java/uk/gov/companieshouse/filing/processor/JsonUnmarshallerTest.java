package uk.gov.companieshouse.filing.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import uk.gov.companieshouse.filing.model.Address;

public class JsonUnmarshallerTest {

    private JsonUnmarshaller unmarshaller = new JsonUnmarshaller();
    
    @Test
    public void testUnmarshallAddressValidAddress() throws IOException {
        final String premises = "2";
        final String poBox = "100";
        final String line1 = "LINE1";
        final String line2 = "LINE2";
        final String postCode = "CF14 3UZ";
        final String country = "Wales";
        final String locality = "Cardiff";
        final String region = "South Glamorgan";
        final StringBuilder json = new StringBuilder("{");
        json.append("\"premises\":\"").append(premises).append("\",");
        json.append("\"address_line_1\":\"").append(line1).append("\",");
        json.append("\"address_line_2\":\"").append(line2).append("\",");
        json.append("\"postal_code\":\"").append(postCode).append("\",");
        json.append("\"country\":\"").append(country).append("\",");
        json.append("\"locality\":\"").append(locality).append("\",");
        json.append("\"region\":\"").append(region).append("\",");
        json.append("\"po_box\":\"").append(poBox).append("\"");
        json.append("}");
        
        Address address = unmarshaller.unmarshallAddress(json.toString());
        
        assertEquals(line1, address.getAddressLine1());
        assertEquals(line2, address.getAddressLine2());
        assertEquals(postCode, address.getPostalCode());
        assertEquals(premises, address.getPremises());
        assertEquals(country, address.getCountry());
        assertEquals(locality, address.getLocality());
        assertEquals(region, address.getRegion());
        assertEquals(poBox, address.getPoBox());
    }
    
    @Test
    public void testUnmarshallAddressInvalidJson() throws IOException {
        assertThrows(IOException.class, () -> unmarshaller.unmarshallAddress("invalid"));
    }
}
