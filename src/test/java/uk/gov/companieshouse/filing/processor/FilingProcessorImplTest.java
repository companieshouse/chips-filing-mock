package uk.gov.companieshouse.filing.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.filing.model.Address;
import uk.gov.companieshouse.filing.model.FilingProcessed;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.PresenterRecord;
import uk.gov.companieshouse.filing.received.SubmissionRecord;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filing.util.DateService;

@ExtendWith(MockitoExtension.class)
public class FilingProcessorImplTest {
    
    private static final Instant INSTANT = ZonedDateTime.of(2020, 2, 17, 23, 9, 56, 250, ZoneOffset.UTC).toInstant();

    private static final String DATE_TIME_STRING = "2020-02-17T23:09:56Z";

    @InjectMocks
    private FilingProcessorImpl processor;
    
    @Mock
    private DateService dateService;
    
    @Mock
    private Unmarshaller unmarshaller;
    
    private Transaction transaction;
    
    private FilingReceived received;
    
    private Address address;
    
    @BeforeEach
    public void setup() {
        transaction = new Transaction();
        transaction.setData("data");
        transaction.setSubmissionId("SUBMISSION-ID");
        
        received = createFilingReceived(transaction);
        
        address = new Address();
    }
    
    @Test
    public void processAcceptedAddressWithoutPostCode() throws Exception {
        when(dateService.now()).thenReturn(INSTANT);

        when(unmarshaller.unmarshallAddress(transaction.getData())).thenReturn(address);

        FilingProcessed processed = processor.process(received);
        
        assertEquals(received.getApplicationId(), processed.getApplicationId());
        assertEquals(received.getChannelId(), processed.getChannelId());
        assertEquals(received.getPresenter().getLanguage(), processed.getPresenterLanguage());
        assertEquals(received.getPresenter().getUserId(), processed.getPresenterId());
        assertEquals(received.getSubmission().getTransactionId(), processed.getTransactionId());
        assertEquals(transaction.getSubmissionId(), processed.getSubmissionId());
        assertEquals(received.getSubmission().getCompanyName(), processed.getCompanyName());
        assertEquals(received.getSubmission().getCompanyNumber(), processed.getCompanyNumber());
        assertEquals(DATE_TIME_STRING, processed.getProcessedAt());
        assertEquals("accepted", processed.getStatus());
        assertTrue(processed.getRejectionEnglish().isEmpty());
        assertTrue(processed.getRejectionWelsh().isEmpty());
    }

    @Test
    public void processAcceptedAddressNotChPostCode() throws Exception {
        when(dateService.now()).thenReturn(INSTANT);

        address.setPostalCode("NR14 3UZ");
        when(unmarshaller.unmarshallAddress(transaction.getData())).thenReturn(address);

        FilingProcessed processed = processor.process(received);
        
        assertEquals(received.getApplicationId(), processed.getApplicationId());
        assertEquals(received.getChannelId(), processed.getChannelId());
        assertEquals(received.getPresenter().getLanguage(), processed.getPresenterLanguage());
        assertEquals(received.getPresenter().getUserId(), processed.getPresenterId());
        assertEquals(received.getSubmission().getTransactionId(), processed.getTransactionId());
        assertEquals(transaction.getSubmissionId(), processed.getSubmissionId());
        assertEquals(received.getSubmission().getCompanyName(), processed.getCompanyName());
        assertEquals(received.getSubmission().getCompanyNumber(), processed.getCompanyNumber());
        assertEquals(DATE_TIME_STRING, processed.getProcessedAt());
        assertEquals("accepted", processed.getStatus());
        assertTrue(processed.getRejectionEnglish().isEmpty());
        assertTrue(processed.getRejectionWelsh().isEmpty());

    }
    
    @Test
    public void processRejectedAddressChPostCode() throws Exception {
        when(dateService.now()).thenReturn(INSTANT);

        address.setPostalCode("CF14 3UZ");
        when(unmarshaller.unmarshallAddress(transaction.getData())).thenReturn(address);

        FilingProcessed processed = processor.process(received);
        
        assertEquals(received.getApplicationId(), processed.getApplicationId());
        assertEquals(received.getChannelId(), processed.getChannelId());
        assertEquals(received.getPresenter().getLanguage(), processed.getPresenterLanguage());
        assertEquals(received.getPresenter().getUserId(), processed.getPresenterId());
        assertEquals(received.getSubmission().getTransactionId(), processed.getTransactionId());
        assertEquals(transaction.getSubmissionId(), processed.getSubmissionId());
        assertEquals(received.getSubmission().getCompanyName(), processed.getCompanyName());
        assertEquals(received.getSubmission().getCompanyNumber(), processed.getCompanyNumber());
        assertEquals(DATE_TIME_STRING, processed.getProcessedAt());
        assertEquals("rejected", processed.getStatus());
        assertTrue(processed.getRejectionEnglish().contains("The postcode you have supplied cannot be Companies House postcode"));
        assertTrue(processed.getRejectionWelsh().contains("Ni all y cod post rydych wedi'i gyflenwi fod yn god post Tŷ'r Cwmnïau"));
    }
    
    @Test
    public void processInvalidTransactionData() throws Exception {
        when(unmarshaller.unmarshallAddress(transaction.getData())).thenThrow(IOException.class);

        assertThrows(FilingProcessingException.class, () -> processor.process(received));
    }

    private FilingReceived createFilingReceived(Transaction... transactions) {
        PresenterRecord presenter = PresenterRecord.newBuilder().setLanguage("en").setUserId("user")
                .setForename("forename").setSurname("surname").build();
        SubmissionRecord submission = SubmissionRecord.newBuilder().setCompanyName("Company").setCompanyNumber("123456")
                .setTransactionId("1234-5678").setReceivedAt("2020-01-20T15:22:24Z").build();
        return FilingReceived.newBuilder().setApplicationId("AppId").setChannelId("chs").setAttempt(1)
                .setPresenter(presenter).setSubmission(submission).setItems(Arrays.asList(transactions)).build();
    }
}
