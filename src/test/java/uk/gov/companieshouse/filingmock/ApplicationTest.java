package uk.gov.companieshouse.filingmock;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
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
import uk.gov.companieshouse.filingmock.model.FilingProcessed;
import uk.gov.companieshouse.filingmock.processor.FilingProcessingException;
import uk.gov.companieshouse.filingmock.processor.FilingProcessor;
import uk.gov.companieshouse.filingmock.reader.FilingReader;
import uk.gov.companieshouse.filingmock.writer.FilingWriter;
import uk.gov.companieshouse.filingmock.writer.FilingWriterException;

@ExtendWith(MockitoExtension.class)
class ApplicationTest {

    @InjectMocks
    private Application app;

    @Mock
    private FilingReader reader;

    @Mock
    private FilingWriter writer;

    @Mock
    private FilingProcessor processor;

    @Test
    void processFilingsNoElements() {
        Collection<FilingReceived> filingsReceived = Collections.emptyList();

        when(reader.read()).thenReturn(filingsReceived);

        app.processFilings();

        verifyNoInteractions(writer, processor);
    }

    @Test
    void processFilings() throws Exception {
        FilingReceived received1 = createFilingReceived("1");
        FilingReceived received2 = createFilingReceived("2");
        Collection<FilingReceived> filingsReceived = Arrays.asList(received1, received2);

        when(reader.read()).thenReturn(filingsReceived);

        FilingProcessed processed1 = createFilingProcessed(received1.getApplicationId());
        FilingProcessed processed2 = createFilingProcessed(received2.getApplicationId());
        FilingProcessed processed3 = createFilingProcessed(received2.getApplicationId());

        when(processor.process(received1)).thenReturn(List.of(processed1));
        when(processor.process(received2)).thenReturn(Arrays.asList(processed2, processed3));

        app.processFilings();

        verify(writer).write(processed1);
        verify(writer).write(processed2);
        verify(writer).write(processed3);
        verifyNoMoreInteractions(writer);
    }

    @Test
    void processFilingsProcessingException() throws Exception {
        FilingReceived received1 = createFilingReceived("1");
        FilingReceived received2 = createFilingReceived("2");
        Collection<FilingReceived> filingsReceived = Arrays.asList(received1, received2);

        when(reader.read()).thenReturn(filingsReceived);

        FilingProcessed processed1 = createFilingProcessed(received1.getApplicationId());
        FilingProcessed processed2 = createFilingProcessed(received2.getApplicationId());

        when(processor.process(received1)).thenThrow(FilingProcessingException.class);
        when(processor.process(received2)).thenReturn(Arrays.asList(processed1, processed2));

        app.processFilings();

        // Verify it still writes subsequent filings
        verify(writer).write(processed1);
        verify(writer).write(processed2);
        verifyNoMoreInteractions(writer);
    }

    @Test
    void processFilingsWritingException() throws Exception {
        FilingReceived received1 = createFilingReceived("1");
        FilingReceived received2 = createFilingReceived("2");
        Collection<FilingReceived> filingsReceived = Arrays.asList(received1, received2);

        when(reader.read()).thenReturn(filingsReceived);

        FilingProcessed processed1 = createFilingProcessed(received1.getApplicationId());
        FilingProcessed processed2 = createFilingProcessed(received2.getApplicationId());
        FilingProcessed processed3 = createFilingProcessed(received2.getApplicationId());

        when(processor.process(received1)).thenReturn(Arrays.asList(processed1, processed2));
        when(processor.process(received2)).thenReturn(List.of(processed3));

        when(writer.write(processed1)).thenThrow(FilingWriterException.class);

        app.processFilings();

        // Verify it still writes subsequent filings
        verify(writer).write(processed2);
        verify(writer).write(processed3);
        verifyNoMoreInteractions(writer);
    }

    private FilingReceived createFilingReceived(String applicationId) {
        SubmissionRecord submission = new SubmissionRecord();
        submission.setTransactionId("T" + applicationId);

        FilingReceived received = new FilingReceived();
        received.setSubmission(submission);
        received.setApplicationId(applicationId);
        return received;
    }

    private FilingProcessed createFilingProcessed(String applicationId) {
        FilingProcessed processed = new FilingProcessed();
        processed.setTransactionId("T" + applicationId);
        processed.setApplicationId(applicationId);
        return processed;
    }
}
