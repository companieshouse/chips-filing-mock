package uk.gov.companieshouse.filingmock.writer;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import uk.gov.companieshouse.filingmock.Application;
import uk.gov.companieshouse.filingmock.model.FilingProcessed;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class FilingWriterImpl implements FilingWriter {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private final RestTemplate rest;

    private final String chsKafkaApiUrl;
    private final String chsKafkaApiKey;

    public FilingWriterImpl(RestTemplate rest, @Value("${kafka.api.url}") String chsKafkaApiUrl, @Value("${kafka.api.key}")
    String chsKafkaApiKey) {
        this.rest = rest;
        this.chsKafkaApiUrl = chsKafkaApiUrl;
        this.chsKafkaApiKey = chsKafkaApiKey;
    }

    @Override
    public boolean write(FilingProcessed filingProcessed) throws FilingWriterException {
        try {
            Map<String, Object> logData = getLoggedData(filingProcessed);
            LOG.trace("Sending response to kafka api", logData);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", chsKafkaApiKey);

            HttpEntity<FilingProcessed> request = new HttpEntity<>(filingProcessed, headers);

            ResponseEntity<Object> response = rest.postForEntity(chsKafkaApiUrl, request, Object.class);
            if (!HttpStatus.CREATED.equals(response.getStatusCode())) {
                throw new FilingWriterException(
                        "Invalid response from Kafka API: " + response.getStatusCode());
            }
            LOG.info("Filing complete", logData);
            return true;
        } catch (RestClientException ex) {
            throw new FilingWriterException(ex);
        }
    }

    private Map<String, Object> getLoggedData(FilingProcessed filingProcessed) {
        Map<String, Object> data = new HashMap<>();
        data.put("transaction_id", filingProcessed.getTransactionId());
        return data;
    }

}
