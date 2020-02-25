package uk.gov.companieshouse.filingmock.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Rejection {

    @JsonProperty(value = "english_reasons")
    @JsonInclude(Include.NON_EMPTY)
    private List<String> englishReasons = new ArrayList<>();

    @JsonProperty(value = "welsh_reasons")
    @JsonInclude(Include.NON_EMPTY)
    private List<String> welshReasons = new ArrayList<>();

    public void addRejection(String english, String welsh) {
        englishReasons.add(english);
        welshReasons.add(welsh);
    }

    public List<String> getEnglishReasons() {
        return Collections.unmodifiableList(englishReasons);
    }

    public List<String> getWelshReasons() {
        return Collections.unmodifiableList(welshReasons);
    }
}
