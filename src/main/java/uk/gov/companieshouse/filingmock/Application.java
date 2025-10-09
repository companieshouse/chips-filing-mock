package uk.gov.companieshouse.filingmock;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Scheduled;

import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filingmock.model.FilingProcessed;
import uk.gov.companieshouse.filingmock.processor.FilingProcessingException;
import uk.gov.companieshouse.filingmock.processor.FilingProcessor;
import uk.gov.companieshouse.filingmock.reader.FilingReader;
import uk.gov.companieshouse.filingmock.writer.FilingWriter;
import uk.gov.companieshouse.filingmock.writer.FilingWriterException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@SpringBootApplication
public class Application {

    public static final String APPLICATION_NAME = "chips-filing-mock";
    
    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME);
    
    // @Value("${application.waitTimeMs:1000}") the annotation parameter must be static final but it is not possible to inject the value
    private static final long SLEEP_TIME = 1000; // ms
    
    private final FilingReader reader;
    private final FilingWriter writer;
    private final FilingProcessor processor;

    @Autowired
    public Application(FilingReader reader, FilingWriter writer, FilingProcessor processor) {
        this.reader = reader;
        this.writer = writer;
        this.processor = processor;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Scheduled(fixedDelay = SLEEP_TIME)
    protected void processFilings() {
        reader.read().stream().map(this::processFiling).flatMap(Collection::stream)
                .filter(Objects::nonNull).forEach(this::writeFiling);
    }

    private Collection<FilingProcessed> processFiling(FilingReceived received) {
        Map<String, Object> data = new HashMap<>();
        if (received.getSubmission() != null) {
            data.put("transaction id", received.getSubmission().getTransactionId());
            data.put("company number", received.getSubmission().getCompanyNumber());
        }
        try {
            LOG.trace("Filing received", data);
            return processor.process(received);
        } catch (FilingProcessingException ex) {
            LOG.error("Failure processing filing", ex, data);
            return Collections.emptyList();
        }
    }

    private void writeFiling(FilingProcessed processed) {
        try {
            writer.write(processed);
        } catch (FilingWriterException ex) {
            Map<String, Object> data = new HashMap<>();
            data.put("transaction id", processed.getTransactionId());
            LOG.error("Failure sending message", ex, data);
        }
    }
}
