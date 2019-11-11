package uk.gov.companieshouse.filing;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws InterruptedException {
        LOG.info(APPLICATION_NAME + " : running...");
        while (running) {
            for (FilingReceived filingReceived : reader.read()) {
                try {
                    writer.write(processor.process(filingReceived));
                } catch (FilingProcessingException e) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("transactionId", filingReceived.getSubmission().getTransactionId());
                    LOG.error("Failure processing message", e, data);
                } catch (FilingWriterException e) {
                    Map<String, Object> data = new HashMap<>();
                    data.put("transactionId", filingReceived.getSubmission().getTransactionId());
                    LOG.error("Failure writing message to queue", e, data);
                }
            }
            Thread.sleep(sleepTime);
        }
    }
}
