package uk.gov.companieshouse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import uk.gov.companieshouse.processor.FilingProcessor;
import uk.gov.companieshouse.reader.FilingReader;
import uk.gov.companieshouse.writer.FilingWriter;
import uk.gov.companieshouse.writer.FilingWriterException;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private FilingReader reader;
    @Autowired
    private FilingWriter writer;
    @Autowired
    private FilingProcessor processor;

    private boolean running = true;
    
    @Value("${application.waitTimeMs:1000}")
    private long sleepTime = 1000; //ms

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        while (running) {
            reader.read().parallelStream()
                .map(processor::process)
                .forEach(p -> {
                    try {
                        writer.write(p);
                    } catch (FilingWriterException e) {
                        // TODO handle exception
                    }
                });
            Thread.sleep(sleepTime);
        }
    }

}
