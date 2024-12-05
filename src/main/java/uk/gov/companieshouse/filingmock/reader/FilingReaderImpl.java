package uk.gov.companieshouse.filingmock.reader;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filingmock.Application;
import uk.gov.companieshouse.kafka.consumer.CHConsumer;
import uk.gov.companieshouse.kafka.consumer.CHKafkaConsumerGroup;
import uk.gov.companieshouse.kafka.consumer.ConsumerConfig;
import uk.gov.companieshouse.kafka.deserialization.DeserializerFactory;
import uk.gov.companieshouse.kafka.exceptions.DeserializationException;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

/**
 * The type Filing reader.
 */
@Component
public class FilingReaderImpl implements FilingReader {

    private static final Logger LOG = LoggerFactory.getLogger(Application.APPLICATION_NAME);
    private final DeserializerFactory deserializerFactory;
    /**
     * The Application name.
     */
    String applicationName;
    /**
     * The Broker address.
     */
    @Value("${kafka.broker.address}")
    String brokerAddress;
    /**
     * The Topic name.
     */
    @Value("${kafka.consumer.topic}")
    String topicName;
    /**
     * The Poll timeout.
     */
    @Value("${kafka.consumer.pollTimeout:100}")
    long pollTimeout = 100;
    /**
     * The Consumer.
     */
    CHConsumer consumer;


    @Autowired
    public FilingReaderImpl(String applicationName, DeserializerFactory deserializerFactory) {
        this.applicationName = applicationName;
        this.deserializerFactory = deserializerFactory;
    }

    /**
     * Init.
     */
    @PostConstruct
    public void init() {
        ConsumerConfig config = new ConsumerConfig();
        config.setBrokerAddresses(new String[]{brokerAddress});
        config.setTopics(Collections.singletonList(topicName));
        config.setPollTimeout(pollTimeout);
        config.setGroupName(applicationName);

        consumer = createConsumer(config);
        consumer.connect();
    }

    /**
     * Create consumer ch consumer.
     *
     * @param config the config
     * @return the ch consumer
     */
    CHConsumer createConsumer(ConsumerConfig config) {
        return new CHKafkaConsumerGroup(config);
    }

    /**
     * Close.
     */
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
            } catch (Exception ex) {
                Map<String, Object> data = new HashMap<>();
                data.put("message", msg.getValue() == null ? "" : new String(msg.getValue()));
                LOG.error("Failed to read message from queue", ex, data);
            }
        }
        return receivedList;
    }

    private FilingReceived deserialise(Message msg) throws DeserializationException {
        return deserializerFactory.getSpecificRecordDeserializer(FilingReceived.class)
                .fromBinary(msg, FilingReceived.getClassSchema());
    }
}
