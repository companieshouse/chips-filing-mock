package uk.gov.companieshouse.filing.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.filing.Application;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.kafka.consumer.CHKafkaConsumerGroup;
import uk.gov.companieshouse.kafka.consumer.ConsumerConfig;
import uk.gov.companieshouse.kafka.deserialization.DeserializerFactory;
import uk.gov.companieshouse.kafka.exceptions.DeserializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class FilingReaderImpl implements FilingReader {
    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    
    @Autowired
    private String applicationName;
    
    @Value("${kafka.broker.address}")
    private String brokerAddress;
    
    @Value("${kafka.consumer.topic}")
    private String topicName;

    @Value("${kafka.consumer.pollTimeout:100}")
    private long pollTimeout = 100;
    
    private CHKafkaConsumerGroup consumer;
    
    @Autowired
    private DeserializerFactory deserializerFactory;

    @PostConstruct
    public void init() {
        ConsumerConfig config = new ConsumerConfig();
        config.setBrokerAddresses(new String[] { brokerAddress });
        config.setTopics(Arrays.asList(topicName));
        config.setPollTimeout(pollTimeout);
        config.setGroupName(applicationName);

        consumer = new CHKafkaConsumerGroup(config);
        consumer.connect();
    }
    
    @PreDestroy
    public void close() {
        consumer.close();
    }

    @Override
    public Collection<FilingReceived> read() {
        List<FilingReceived> receivedList = new ArrayList<>();
        for (Message msg : consumer.consume()) {
            try {
                receivedList.add(deserialise(msg));
            } catch (Exception e) {
                LOG.error("Failed to read message from queue", e);
            }
        }
        return receivedList;
    }
    
    private FilingReceived deserialise(Message msg) throws DeserializationException {
        return deserializerFactory.getSpecificRecordDeserializer(FilingReceived.class).fromBinary(msg, FilingReceived.getClassSchema());
    }
}
