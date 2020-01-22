package uk.gov.companieshouse.filing.util;

import java.util.Date;

public class DateServiceImpl implements DateService {

    @Override
    public Date now() {
        return new Date();
    }

}
