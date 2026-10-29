package uk.gov.companieshouse.filingmock.writer;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.filingmock.model.FilingProcessed;

@ExtendWith(MockitoExtension.class)
class FilingWriterImplTest {

    private static final String KAFKA_API_URL = "http://kafka-api-url:1234";
    private static final String KAFKA_API_KEY = "http://kafka-api-url:1234";

    private FilingWriterImpl writer;

    @Mock
    private RestTemplate rest;

    private FilingProcessed filingProcessed;

    @BeforeEach
    void setup() {
        writer =  new FilingWriterImpl(rest, KAFKA_API_URL, KAFKA_API_KEY);
        filingProcessed = new FilingProcessed();
    }

    @Test
    void write() throws Exception {
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.CREATED);

        HttpEntity<FilingProcessed> request = getFilingProcessedHttpEntity();

        when(rest.postForEntity(eq(KAFKA_API_URL), eq(request), any())).thenReturn(
                response);

        assertTrue(writer.write(filingProcessed));

        verify(rest).postForEntity(eq(KAFKA_API_URL), any(), any());
    }

    private @NonNull HttpEntity<FilingProcessed> getFilingProcessedHttpEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", KAFKA_API_KEY);
        return new HttpEntity<>(filingProcessed, headers);
    }

    @Test
    void writeErrorResponse() {
        ResponseEntity<Object> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        HttpEntity<FilingProcessed> request = getFilingProcessedHttpEntity();

        when(rest.postForEntity(eq(KAFKA_API_URL), eq(request), any())).thenReturn(
                response);

        assertThrows(FilingWriterException.class, () -> writer.write(filingProcessed));
    }

    @Test
    void writeRestException() {

        HttpEntity<FilingProcessed> request = getFilingProcessedHttpEntity();

        when(rest.postForEntity(eq(KAFKA_API_URL), eq(request), any())).thenThrow(
                new RestClientException("exception"));

        assertThrows(FilingWriterException.class, () -> writer.write(filingProcessed));
    }
}
