package uk.gov.companieshouse.filing.util;

import java.util.Date;

import org.springframework.stereotype.Service;

@Service
public class DateServiceImpl implements DateService {

    @Override
    public Date now() {
        return new Date();
    }

}
