package uk.gov.companieshouse.filing;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.companieshouse.filing.processed.FilingProcessed;
import uk.gov.companieshouse.filing.processor.FilingProcessingException;
import uk.gov.companieshouse.filing.processor.FilingProcessor;
import uk.gov.companieshouse.filing.reader.FilingReader;
import uk.gov.companieshouse.filing.received.FilingReceived;
import uk.gov.companieshouse.filing.writer.FilingWriter;
import uk.gov.companieshouse.filing.writer.FilingWriterException;

@ExtendWith(MockitoExtension.class)
public class ApplicationTest {

    @InjectMocks
    private Application app;

    @Mock
    private FilingReader reader;

    @Mock
    private FilingWriter writer;

    @Mock
    private FilingProcessor processor;

    @Test
    public void processFilingsNoElements() {
        Collection<FilingReceived> filingsReceived = Collections.emptyList();

        when(reader.read()).thenReturn(filingsReceived);

        app.processFilings();

        verifyNoInteractions(writer, processor);
    }

    @Test
    public void processFilings() throws Exception {
        FilingReceived received1 = createFilingReceived("1");
        FilingReceived received2 = createFilingReceived("2");
        Collection<FilingReceived> filingsReceived = Arrays.asList(received1, received2);

        when(reader.read()).thenReturn(filingsReceived);

        FilingProcessed processed1 = createFilingProcessed(received1.getApplicationId());
        FilingProcessed processed2 = createFilingProcessed(received2.getApplicationId());

        when(processor.process(received1)).thenReturn(processed1);
        when(processor.process(received2)).thenReturn(processed2);

        app.processFilings();

        verify(writer).write(processed1);
        verify(writer).write(processed2);
        verifyNoMoreInteractions(writer);
    }

    @Test
    public void processFilingsProcessingException() throws Exception {
        FilingReceived received1 = createFilingReceived("1");
        FilingReceived received2 = createFilingReceived("2");
        Collection<FilingReceived> filingsReceived = Arrays.asList(received1, received2);

        when(reader.read()).thenReturn(filingsReceived);

        FilingProcessed processed2 = createFilingProcessed(received2.getApplicationId());

        when(processor.process(received1)).thenThrow(FilingProcessingException.class);
        when(processor.process(received2)).thenReturn(processed2);

        app.processFilings();

        // Verify it still writes subsequent filings
        verify(writer).write(processed2);
        verifyNoMoreInteractions(writer);
    }
    
    @Test
    public void processFilingsWritingException() throws Exception {
        FilingReceived received1 = createFilingReceived("1");
        FilingReceived received2 = createFilingReceived("2");
        Collection<FilingReceived> filingsReceived = Arrays.asList(received1, received2);

        when(reader.read()).thenReturn(filingsReceived);

        FilingProcessed processed1 = createFilingProcessed(received1.getApplicationId());
        FilingProcessed processed2 = createFilingProcessed(received2.getApplicationId());

        when(processor.process(received1)).thenReturn(processed1);
        when(processor.process(received2)).thenReturn(processed2);
        
        when(writer.write(processed1)).thenThrow(FilingWriterException.class);

        app.processFilings();

        // Verify it still writes subsequent filings
        verify(writer).write(processed2);
        verifyNoMoreInteractions(writer);
    }

    private FilingReceived createFilingReceived(String applicationId) {
        uk.gov.companieshouse.filing.received.SubmissionRecord submission = new uk.gov.companieshouse.filing.received.SubmissionRecord();
        submission.setTransactionId("T" + applicationId);

        FilingReceived received = new FilingReceived();
        received.setSubmission(submission);
        received.setApplicationId(applicationId);
        return received;
    }
    
    private FilingProcessed createFilingProcessed(String applicationId) {
        uk.gov.companieshouse.filing.processed.SubmissionRecord submission = new uk.gov.companieshouse.filing.processed.SubmissionRecord();
        submission.setTransactionId("T" + applicationId);

        FilingProcessed processed = new FilingProcessed();
        processed.setSubmission(submission);
        processed.setApplicationId(applicationId);
        return processed;
    }
}
