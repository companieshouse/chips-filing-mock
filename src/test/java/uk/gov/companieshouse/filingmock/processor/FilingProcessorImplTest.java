package uk.gov.companieshouse.filingmock.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

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
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.received.PresenterRecord;
import uk.gov.companieshouse.filing.received.SubmissionRecord;
import uk.gov.companieshouse.filing.received.Transaction;
import uk.gov.companieshouse.filingmock.model.FilingProcessed;
import uk.gov.companieshouse.filingmock.model.FilingStatus;
import uk.gov.companieshouse.filingmock.model.Status;
import uk.gov.companieshouse.filingmock.processor.strategy.AcceptanceStrategy;
import uk.gov.companieshouse.filingmock.processor.strategy.AcceptanceStrategyException;
import uk.gov.companieshouse.filingmock.util.DateService;

@ExtendWith(MockitoExtension.class)
class FilingProcessorImplTest {

    private static final Instant INSTANT = ZonedDateTime.of(2020, 2, 17, 23, 9, 56, 250, ZoneOffset.UTC).toInstant();

    private static final String DATE_TIME_STRING = "2020-02-17T23:09:56Z";

    @InjectMocks
    @Spy
    private FilingProcessorImpl processor;
    
    @Mock
    private DateService dateService;
    
    @Mock
    private AcceptanceStrategy acceptanceStrategy;

    @BeforeEach
    void setup() {
        when(dateService.now()).thenReturn(INSTANT);
        doReturn(acceptanceStrategy).when(processor).getStrategy(any());
    }

    @Test
    void processAcceptedFiling() throws Exception {
        Transaction transaction = createTransaction("1");
        FilingReceived received = createFilingReceived(transaction);

        FilingStatus filingStatus = new FilingStatus();
        filingStatus.setStatus(Status.ACCEPTED);
        when(acceptanceStrategy.accept(transaction)).thenReturn(filingStatus);

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
        assertEquals(Status.ACCEPTED, processed.getStatus());
        assertNull(processed.getRejection());
    }

    @Test
    void processRejectedFiling() throws Exception {
        Transaction transaction = createTransaction("1");
        FilingReceived received = createFilingReceived(transaction);

        FilingStatus filingStatus = new FilingStatus();
        filingStatus.setStatus(Status.REJECTED);
        filingStatus.addRejection("English rejection", "Gwrthod gymraeg");
        when(acceptanceStrategy.accept(transaction)).thenReturn(filingStatus);

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
        assertEquals(Status.REJECTED, processed.getStatus());
        assertTrue(processed.getRejection().getEnglishReasons().contains("English rejection"));
        assertTrue(processed.getRejection().getWelshReasons().contains("Gwrthod gymraeg"));
    }

    @Test
    void processInvalidTransactionData() throws Exception {
        Transaction transaction = createTransaction("1");
        FilingReceived received = createFilingReceived(transaction);

        when(acceptanceStrategy.accept(transaction)).thenThrow(AcceptanceStrategyException.class);

        assertThrows(FilingProcessingException.class, () -> processor.process(received));
    }

    @Test
    void processFilingMultipleSubmissions() throws Exception {
        Transaction transaction1 = createTransaction("1");
        Transaction transaction2 = createTransaction("2");
        Transaction transaction3 = createTransaction("3");
        FilingReceived received = createFilingReceived(transaction1, transaction2, transaction3);

        FilingStatus filingStatus = new FilingStatus();
        filingStatus.setStatus(Status.ACCEPTED);
        when(acceptanceStrategy.accept(transaction1)).thenReturn(filingStatus);
        when(acceptanceStrategy.accept(transaction2)).thenReturn(filingStatus);
        when(acceptanceStrategy.accept(transaction3)).thenReturn(filingStatus);
        //TODO rgarcia reject one of the transactions

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
            assertEquals(Status.ACCEPTED, processed.getStatus());
            assertNull(processed.getRejection());
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
