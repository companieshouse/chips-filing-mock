package uk.gov.companieshouse.filing.util;

import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class DateServiceImpl implements DateService {

    @Override
    public Instant now() {
        return Instant.now();
    }
}
