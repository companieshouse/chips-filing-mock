package uk.gov.companieshouse.filingmock.writer;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${kafka.api.url}")
    String kafkaApiUrl;

    private final RestTemplate rest;

    @Autowired
    public FilingWriterImpl(RestTemplate rest) {
        this.rest = rest;
    }

    @Override
    public boolean write(FilingProcessed filingProcessed) throws FilingWriterException {
        try {
            Map<String, Object> logData = getLoggedData(filingProcessed);
            LOG.trace("Sending response to kafka api", logData);

            ResponseEntity<Object> response = rest.postForEntity(kafkaApiUrl, filingProcessed,
                    Object.class);
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
