package uk.gov.companieshouse.filingmock.writer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FilingWriterImplTest {
    
    @InjectMocks
    private FilingWriterImpl writer;
    
    @Test
    public void write() throws Exception {
        // TODO: implement unit test
    }
}
