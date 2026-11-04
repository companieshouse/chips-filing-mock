package uk.gov.companieshouse.filingmock.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Class to hold the data for a Dissolution (DS01 / LLDS01) filing.
 */
public class DissolutionFilingData {

    @JsonProperty("officers")
    private List<Officer> officers;

    public List<Officer> getOfficers() {
        return officers;
    }

    public static class Officer {

        @JsonProperty("on_behalf_name")
        private String onBehalfName;

        public String getOnBehalfName() {
            return onBehalfName;
        }
    }
}
