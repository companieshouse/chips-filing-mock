package uk.gov.companieshouse.filingmock.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.PresenterRecord;
import uk.gov.companieshouse.filing.received.SubmissionRecord;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.Address;
import uk.gov.companieshouse.filingmock.model.FilingProcessed;
import uk.gov.companieshouse.filingmock.processor.FilingProcessingException;
import uk.gov.companieshouse.filingmock.processor.FilingProcessorImpl;
import uk.gov.companieshouse.filingmock.processor.Unmarshaller;
import uk.gov.companieshouse.filingmock.util.DateService;

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
    
    private Address address;
    
    @BeforeEach
    public void setup() {
        address = new Address();
    }
    
    @Test
    public void processAcceptedAddressWithoutPostCode() throws Exception {
        when(dateService.now()).thenReturn(INSTANT);

        Transaction transaction = createTransaction("1");
        FilingReceived received = createFilingReceived(transaction);

        when(unmarshaller.unmarshallAddress(transaction.getData())).thenReturn(address);

        List<FilingProcessed> processedResult = processor.process(received);
        
        assertEquals(1, processedResult.size());
        FilingProcessed processed = processedResult.get(0);
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

        Transaction transaction = createTransaction("1");
        FilingReceived received = createFilingReceived(transaction);
        
        address.setPostalCode("NR14 3UZ");
        when(unmarshaller.unmarshallAddress(transaction.getData())).thenReturn(address);

        List<FilingProcessed> processedResult = processor.process(received);
        
        assertEquals(1, processedResult.size());
        FilingProcessed processed = processedResult.get(0);
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
    public void processRejectedAddressChPostCodeWales() throws Exception {
    	postCodeTest("CF14 3UZ");
    }
    
    @Test
    public void processRejectedAddressChPostCodeEngland() throws Exception {
    	postCodeTest("SW1H 9EX");
    }
    
    @Test
    public void processRejectedAddressChPostCodeNorthernIreland() throws Exception {
    	postCodeTest("BT2 8BG");
    }
    
    @Test
    public void processRejectedAddressChPostCodeScotland() throws Exception {
    	postCodeTest("EH3 9FF");
    }
    
    private void postCodeTest(String postCode) throws Exception {
    	when(dateService.now()).thenReturn(INSTANT);
    	Transaction transaction = createTransaction("1");
        FilingReceived received = createFilingReceived(transaction);
        
        address.setPostalCode(postCode);
        when(unmarshaller.unmarshallAddress(transaction.getData())).thenReturn(address);

        List<FilingProcessed> processedResult = processor.process(received);
        
        assertEquals(1, processedResult.size());
        FilingProcessed processed = processedResult.get(0);
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
        Transaction transaction = createTransaction("1");
        FilingReceived received = createFilingReceived(transaction);

        when(unmarshaller.unmarshallAddress(transaction.getData())).thenThrow(IOException.class);

        assertThrows(FilingProcessingException.class, () -> processor.process(received));
    }
    
    @Test
    public void processFilingMultipleSubmissions() throws Exception {
        when(dateService.now()).thenReturn(INSTANT);
        Transaction transaction1 = createTransaction("1");
        Transaction transaction2 = createTransaction("2");
        Transaction transaction3 = createTransaction("3");
        FilingReceived received = createFilingReceived(transaction1, transaction2, transaction3);

        when(unmarshaller.unmarshallAddress(transaction1.getData())).thenReturn(address);
        when(unmarshaller.unmarshallAddress(transaction2.getData())).thenReturn(new Address());
        when(unmarshaller.unmarshallAddress(transaction3.getData())).thenReturn(new Address());

        List<FilingProcessed> processedResult = processor.process(received);
        assertEquals(received.getItems().size(), processedResult.size());
        for (int i=0; i<received.getItems().size(); i++) {
            FilingProcessed processed = processedResult.get(i);
            assertEquals(received.getApplicationId(), processed.getApplicationId());
            assertEquals(received.getChannelId(), processed.getChannelId());
            assertEquals(received.getPresenter().getLanguage(), processed.getPresenterLanguage());
            assertEquals(received.getPresenter().getUserId(), processed.getPresenterId());
            assertEquals(received.getSubmission().getTransactionId(), processed.getTransactionId());
            assertEquals(received.getItems().get(i).getSubmissionId(), processed.getSubmissionId());
            assertEquals(received.getSubmission().getCompanyName(), processed.getCompanyName());
            assertEquals(received.getSubmission().getCompanyNumber(), processed.getCompanyNumber());
            assertEquals(DATE_TIME_STRING, processed.getProcessedAt());
            assertEquals("accepted", processed.getStatus());
            assertTrue(processed.getRejectionEnglish().isEmpty());
            assertTrue(processed.getRejectionWelsh().isEmpty());
        }
    }

    private FilingReceived createFilingReceived(Transaction... transactions) {
        PresenterRecord presenter = PresenterRecord.newBuilder().setLanguage("en").setUserId("user")
                .setForename("forename").setSurname("surname").build();
        SubmissionRecord submission = SubmissionRecord.newBuilder().setCompanyName("Company").setCompanyNumber("123456")
                .setTransactionId("1234-5678").setReceivedAt("2020-01-20T15:22:24Z").build();
        return FilingReceived.newBuilder().setApplicationId("AppId").setChannelId("chs").setAttempt(1)
                .setPresenter(presenter).setSubmission(submission).setItems(Arrays.asList(transactions)).build();
    }
    
    private Transaction createTransaction(String id) {
        Transaction transaction = new Transaction();
        transaction.setData("data"+ id);
        transaction.setSubmissionId(id);
        return transaction;
    }

}
