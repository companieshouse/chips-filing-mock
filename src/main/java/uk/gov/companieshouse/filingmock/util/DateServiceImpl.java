package uk.gov.companieshouse.filingmock.util;

import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class DateServiceImpl implements DateService {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
