package uk.gov.companieshouse.filing.reader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.SubmissionRecord;
import uk.gov.companieshouse.kafka.consumer.CHConsumer;
import uk.gov.companieshouse.kafka.deserialization.AvroDeserializer;
import uk.gov.companieshouse.kafka.deserialization.DeserializerFactory;
import uk.gov.companieshouse.kafka.exceptions.DeserializationException;
import uk.gov.companieshouse.kafka.message.Message;

@ExtendWith(MockitoExtension.class)
public class FilingReaderImplTest {

    @InjectMocks
    private FilingReaderImpl reader;

    @Mock
    private CHConsumer consumer;

    @Mock
    private DeserializerFactory deserializerFactory;

    @Mock
    private AvroDeserializer<FilingReceived> deserializer;

    @Test
    public void readNoMessage() {
        List<Message> messages = Collections.emptyList();
        when(consumer.consume()).thenReturn(messages);

        Collection<FilingReceived> result = reader.read();

        assertTrue(result.isEmpty());
    }

    @Test
    public void readValidMessages() throws Exception {
        when(deserializerFactory.getSpecificRecordDeserializer(FilingReceived.class)).thenReturn(deserializer);

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
    public void readInvalidMessage() throws Exception {
        when(deserializerFactory.getSpecificRecordDeserializer(FilingReceived.class)).thenReturn(deserializer);

        List<Message> messages = new ArrayList<>();
        Message message1 = new Message();
        messages.add(message1);
        Message message2 = new Message();
        messages.add(message2);
        when(consumer.consume()).thenReturn(messages);

        FilingReceived filing2 = createFilingReceived("1");
        when(deserializer.fromBinary(message1, FilingReceived.SCHEMA$)).thenThrow(DeserializationException.class);
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
