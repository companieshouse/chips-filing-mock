package uk.gov.companieshouse.filingmock.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.SubmissionRecord;
import uk.gov.companieshouse.kafka.consumer.CHConsumer;
import uk.gov.companieshouse.kafka.consumer.ConsumerConfig;
import uk.gov.companieshouse.kafka.deserialization.AvroDeserializer;
import uk.gov.companieshouse.kafka.deserialization.DeserializerFactory;
import uk.gov.companieshouse.kafka.exceptions.DeserializationException;
import uk.gov.companieshouse.kafka.message.Message;

@ExtendWith(MockitoExtension.class)
class FilingReaderImplTest {

    @Spy
    @InjectMocks
    private FilingReaderImpl reader;

    @Mock
    private CHConsumer consumer;

    @Mock
    private DeserializerFactory deserializerFactory;

    @Mock
    private AvroDeserializer<FilingReceived> deserializer;

    @BeforeEach
    void init() {
        doReturn(consumer).when(reader).createConsumer(Mockito.any());
        reader.brokerAddress = "kafka address";
        reader.topicName = "filing-received";
        reader.applicationName = "chips filing mock";

        reader.init();

        assertEquals(consumer, reader.consumer);
        verify(consumer).connect();

        ArgumentCaptor<ConsumerConfig> consumerConfigCaptor = ArgumentCaptor.forClass(
                ConsumerConfig.class);
        verify(reader).createConsumer(consumerConfigCaptor.capture());
        ConsumerConfig config = consumerConfigCaptor.getValue();
        assertNotNull(config.getBrokerAddresses());
        assertEquals(1, config.getBrokerAddresses().length);
        assertEquals(reader.brokerAddress, config.getBrokerAddresses()[0]);
        assertNotNull(config.getTopics());
        assertEquals(1, config.getTopics().size());
        assertEquals(reader.topicName, config.getTopics().getFirst());
        assertEquals(reader.applicationName, config.getGroupName());
        assertEquals(reader.pollTimeout, config.getPollTimeout());
    }

    @Test
    void readNoMessage() {
        List<Message> messages = Collections.emptyList();
        when(consumer.consume()).thenReturn(messages);

        Collection<FilingReceived> result = reader.read();

        assertTrue(result.isEmpty());
    }

    @Test
    void readValidMessages() throws Exception {
        when(deserializerFactory.getSpecificRecordDeserializer(FilingReceived.class)).thenReturn(
                deserializer);

        List<Message> messages = new ArrayList<>();
        Message message1 = new Message();
        messages.add(message1);
        Message message2 = new Message();
        messages.add(message2);
        when(consumer.consume()).thenReturn(messages);

        FilingReceived filing1 = createFilingReceived("1");
        FilingReceived filing2 = createFilingReceived("1");
        when(deserializer.fromBinary(message1, FilingReceived.SCHEMA$)).thenReturn(filing1);
        when(deserializer.fromBinary(message2, FilingReceived.SCHEMA$)).thenReturn(filing2);

        Collection<FilingReceived> result = reader.read();

        assertEquals(messages.size(), result.size());
        assertTrue(result.contains(filing1));
        assertTrue(result.contains(filing2));
    }

    @Test
    void readInvalidMessage() throws Exception {
        when(deserializerFactory.getSpecificRecordDeserializer(FilingReceived.class)).thenReturn(
                deserializer);

        List<Message> messages = new ArrayList<>();
        Message message1 = new Message();
        messages.add(message1);
        Message message2 = new Message();
        messages.add(message2);
        when(consumer.consume()).thenReturn(messages);

        FilingReceived filing2 = createFilingReceived("1");
        when(deserializer.fromBinary(message1, FilingReceived.SCHEMA$)).thenThrow(
                DeserializationException.class);
        when(deserializer.fromBinary(message2, FilingReceived.SCHEMA$)).thenReturn(filing2);

        Collection<FilingReceived> result = reader.read();

        // assert it subsequent messages
        assertEquals(1, result.size());
        assertTrue(result.contains(filing2));
    }

    private FilingReceived createFilingReceived(String applicationId) {
        SubmissionRecord submission = new SubmissionRecord();
        submission.setTransactionId("T" + applicationId);

        FilingReceived received = new FilingReceived();
        received.setSubmission(submission);
        received.setApplicationId(applicationId);
        return received;
    }

}
