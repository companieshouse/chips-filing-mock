package uk.gov.companieshouse.filing;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import uk.gov.companieshouse.filing.model.FilingProcessed;
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
        new SpringApplicationBuilder(Application.class).web(WebApplicationType.NONE).run(args);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        LOG.info(APPLICATION_NAME + " : running...");
        while (running) {
            processFilings();
            Thread.sleep(sleepTime);
        }
    }

    protected void processFilings() {
        reader.read().stream().map(this::processFiling).filter(Objects::nonNull).forEach(this::writeFiling);
    }

    private FilingProcessed processFiling(FilingReceived received) {
        Map<String, Object> data = new HashMap<>();
        if (received.getSubmission() != null) {
            data.put("transaction id", received.getSubmission().getTransactionId());
            data.put("company number", received.getSubmission().getCompanyNumber());
        }
        try {
            LOG.trace("Filing received", data);
            return processor.process(received);
        } catch (FilingProcessingException e) {
            LOG.error("Failure processing filing", e, data);
            return null;
        }
    }

    private void writeFiling(FilingProcessed processed) {
        try {
            writer.write(processed);
        } catch (FilingWriterException e) {
            Map<String, Object> data = new HashMap<>();
            data.put("transaction id", processed.getTransactionId());
            LOG.error("Failure sending message", e, data);
        }
    }
}
