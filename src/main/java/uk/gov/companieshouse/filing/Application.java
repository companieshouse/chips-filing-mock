package uk.gov.companieshouse.filing;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processor.FilingProcessingException;
import uk.gov.companieshouse.filing.processor.FilingProcessor;
import uk.gov.companieshouse.filing.reader.FilingReader;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.writer.FilingWriter;
import uk.gov.companieshouse.filing.writer.FilingWriterException;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@SpringBootApplication
public class Application implements CommandLineRunner {

    public static final String APPLICATION_NAME = "chips-filing-mock";
    private static final Logger LOG = LoggerFactory.getLogger(APPLICATION_NAME);

    @Autowired
    private FilingReader reader;
    @Autowired
    private FilingWriter writer;
    @Autowired
    private FilingProcessor processor;

    private boolean running = true;

    @Value("${application.waitTimeMs:1000}")
    private long sleepTime = 1000; // ms

    public static void main(String[] args) {
        SpringApplication.run(Application.class, new String[0]);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        LOG.info(APPLICATION_NAME + " : running...");
        while (running) {
            processFilings();
            Thread.sleep(sleepTime);
        }
    }

    public void processFilings() {
        reader.read().stream().map(this::processFiling).filter(Objects::nonNull).forEach(this::writeFiling);
    }

    private FilingProcessed processFiling(FilingReceived received) {
        try {
            return processor.process(received);
        } catch (FilingProcessingException e) {
            Map<String, Object> data = new HashMap<>();
            data.put("transactionId", received.getSubmission().getTransactionId());
            LOG.error("Failure processing message", e, data);
            return null;
        }
    }

    private void writeFiling(FilingProcessed processed) {
        try {
            writer.write(processed);
        } catch (FilingWriterException e) {
            Map<String, Object> data = new HashMap<>();
            data.put("transactionId", processed.getSubmission().getTransactionId());
            LOG.error("Failure processing message", e, data);
        }
    }
}
