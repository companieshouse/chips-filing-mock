package uk.gov.companieshouse.filing.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.filing.processed.FilingProcessed;
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
    
    @Test
    public void processAcceptedAddressWithoutPostCode() throws Exception {
        when(dateService.now()).thenReturn(INSTANT);

        Transaction transaction = new Transaction();
        transaction.setData("{}");
        FilingReceived received = createFilingReceived(transaction);

        FilingProcessed processed = processor.process(received);
        
        assertEquals(received.getApplicationId(), processed.getApplicationId());
        assertEquals(1, processed.getAttempt());
        assertEquals(received.getChannelId(), processed.getChannelId());
        assertEquals(received.getPresenter().getLanguage(), processed.getPresenter().getLanguage());
        assertEquals(received.getPresenter().getUserId(), processed.getPresenter().getUserId());
        assertEquals(received.getSubmission().getTransactionId(), processed.getSubmission().getTransactionId());
        assertEquals(received.getSubmission().getCompanyName(), processed.getResponse().getCompanyName());
        assertEquals(received.getSubmission().getCompanyNumber(), processed.getResponse().getCompanyNumber());
        assertEquals(DATE_TIME_STRING, processed.getResponse().getProcessedAt());
        assertEquals(DATE_TIME_STRING, processed.getResponse().getDateOfCreation());
        assertEquals("accepted", processed.getResponse().getStatus());
        assertNull(processed.getResponse().getReject());
    }

    @Test
    public void processAcceptedAddressNotChPostCode() throws Exception {
        when(dateService.now()).thenReturn(INSTANT);

        Transaction transaction = new Transaction();
        transaction.setData("{\"postal_code\":\"NR14 3UZ\"}");
        FilingReceived received = createFilingReceived(transaction);

        FilingProcessed processed = processor.process(received);
        
        assertEquals(received.getApplicationId(), processed.getApplicationId());
        assertEquals(1, processed.getAttempt());
        assertEquals(received.getChannelId(), processed.getChannelId());
        assertEquals(received.getPresenter().getLanguage(), processed.getPresenter().getLanguage());
        assertEquals(received.getPresenter().getUserId(), processed.getPresenter().getUserId());
        assertEquals(received.getSubmission().getTransactionId(), processed.getSubmission().getTransactionId());
        assertEquals(received.getSubmission().getCompanyName(), processed.getResponse().getCompanyName());
        assertEquals(received.getSubmission().getCompanyNumber(), processed.getResponse().getCompanyNumber());
        assertEquals(DATE_TIME_STRING, processed.getResponse().getProcessedAt());
        assertEquals(DATE_TIME_STRING, processed.getResponse().getDateOfCreation());
        assertEquals("accepted", processed.getResponse().getStatus());
        assertNull(processed.getResponse().getReject());
    }
    
    @Test
    public void processRejectedAddressChPostCode() throws Exception {
        when(dateService.now()).thenReturn(INSTANT);

        Transaction transaction = new Transaction();
        transaction.setData("{\"postal_code\":\"CF14 3UZ\"}");
        FilingReceived received = createFilingReceived(transaction);

        FilingProcessed processed = processor.process(received);
        
        assertEquals(received.getApplicationId(), processed.getApplicationId());
        assertEquals(1, processed.getAttempt());
        assertEquals(received.getChannelId(), processed.getChannelId());
        assertEquals(received.getPresenter().getLanguage(), processed.getPresenter().getLanguage());
        assertEquals(received.getPresenter().getUserId(), processed.getPresenter().getUserId());
        assertEquals(received.getSubmission().getTransactionId(), processed.getSubmission().getTransactionId());
        assertEquals(received.getSubmission().getCompanyName(), processed.getResponse().getCompanyName());
        assertEquals(received.getSubmission().getCompanyNumber(), processed.getResponse().getCompanyNumber());
        assertEquals(DATE_TIME_STRING, processed.getResponse().getProcessedAt());
        assertEquals(DATE_TIME_STRING, processed.getResponse().getDateOfCreation());
        assertEquals("rejected", processed.getResponse().getStatus());
        assertNotNull(processed.getResponse().getReject());
        assertTrue(processed.getResponse().getReject().getReasonsEnglish().contains("The postcode you have supplied cannot be Companies House postcode"));
        assertTrue(processed.getResponse().getReject().getReasonsWelsh().contains("Ni all y cod post rydych wedi'i gyflenwi fod yn god post Tŷ'r Cwmnïau"));
    }
    
    @Test
    public void processInvalidTransactionData() throws Exception {
        when(dateService.now()).thenReturn(INSTANT);
        
        Transaction transaction = new Transaction();
        transaction.setData("invalid data");
        FilingReceived received = createFilingReceived(transaction);

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
