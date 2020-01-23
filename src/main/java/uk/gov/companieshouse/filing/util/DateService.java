package uk.gov.companieshouse.filing.util;

import java.time.Instant;

public interface DateService {

    /**
     * Obtains the current instant
     * 
     * @return the current instant, not null
     */
    Instant now();

}
