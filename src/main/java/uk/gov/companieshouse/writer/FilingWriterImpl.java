package uk.gov.companieshouse.writer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.kafka.message.Message;
import uk.gov.companieshouse.kafka.producer.Acks;
import uk.gov.companieshouse.kafka.producer.CHKafkaProducer;
import uk.gov.companieshouse.kafka.producer.ProducerConfig;

@Component
public class FilingWriterImpl implements FilingWriter {

    @Value("${kafka.broker.address}")
    private String brokerAddress;
    
    @Value("${kafka.topic.filing.processed}")
    private String topicName;
    
    private CHKafkaProducer producer;

    @PostConstruct
    public void init() {
        ProducerConfig config = new ProducerConfig();
        config.setBrokerAddresses(new String[] { brokerAddress });
        config.setAcks(Acks.WAIT_FOR_LOCAL);
        config.setRetries(10);
        config.setRoundRobinPartitioner(false);

        producer = new CHKafkaProducer(config);

        // TODO: close the producer?
    }

    @Override
    public boolean write(FilingProcessed filingProcessed) throws FilingWriterException {
        Message msg = getMessage(filingProcessed);
        try {
            producer.send(msg);
        } catch (Exception e) {
            throw new FilingWriterException(e);
        }
        return true;
    }


    private Message getMessage(FilingProcessed filingProcessed) {
        Message m = new Message();
        m.setTopic(topicName);
        m.setValue(serialise(filingProcessed));
        return m;
    }
    
    private byte[] serialise(FilingProcessed filingProcessed) {
        //TODO implement
        return filingProcessed.getApplicationId().getBytes();
    }

}
