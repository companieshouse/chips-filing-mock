package uk.gov.companieshouse.reader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.kafka.consumer.CHKafkaConsumerGroup;
import uk.gov.companieshouse.kafka.consumer.ConsumerConfig;
import uk.gov.companieshouse.kafka.message.Message;

@Component
public class FilingReaderImpl implements FilingReader {
    
    @Value("${kafka.broker.address}")
    private String brokerAddress;
    
    @Value("${kafka.topic.filing.received}")
    private String topicName;
    
    @Value("${application.name}")
    private String applicationName;
    
    private CHKafkaConsumerGroup consumer;

    @PostConstruct
    public void init() {
        ConsumerConfig config = new ConsumerConfig();
        config.setBrokerAddresses(new String[] { brokerAddress });
        config.setTopics(Arrays.asList(topicName));
        config.setPollTimeout(100);
        config.setGroupName(applicationName);

        consumer = new CHKafkaConsumerGroup(config);
        consumer.connect();
        // TODO: close the consumer?
    }

    @Override
    public Collection<FilingReceived> read() {
        List<FilingReceived> receivedList = new ArrayList<>();
        for (Message msg : consumer.consume()) {
            receivedList.add(deserialise(msg));
        }
        return receivedList;
    }
    
    private FilingReceived deserialise(Message msg) {
        //TODO implement
        FilingReceived r = new FilingReceived();
        r.setApplicationId(new String(msg.getValue()));
        return r;
    }
}
