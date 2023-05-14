package com.example;

import com.example.service.AWSS3UploadService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class EpikastLogToS3CommandTest {

    @Mock
    private AWSS3UploadService awsS3UploadService;

    @InjectMocks
    private EpikastLogToS3Command epikastLogToS3Command;

    /**
     * Initializes all the mock objects that are defined in the current test class and assigns them to the corresponding fields annotated with @Mock.
     */
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Test for valid and invalid params.
     */
    @Test
    public void testValidUrl() {
        assertTrue(epikastLogToS3Command.isValidUrl("http://80.90.47.7/anupam.acrylic_16.apk.diffoscope.txt"));
        assertTrue(epikastLogToS3Command.isValidUrl("http://www.almhuette-raith.at/apache-log/access.log"));
        assertFalse(epikastLogToS3Command.isValidUrl("http://www.example.com/path/to/file.jpg"));
        assertFalse(epikastLogToS3Command.isValidUrl("http://www.example.com/path/to/"));
        assertFalse(epikastLogToS3Command.isValidUrl("exa@mple.com"));
        assertFalse(epikastLogToS3Command.isValidUrl("htt://example.com"));
        assertFalse(epikastLogToS3Command.isValidUrl("example.com"));
        assertFalse(epikastLogToS3Command.isValidUrl(" "));
        assertFalse(epikastLogToS3Command.isValidUrl(null));
    }

    /**
     * Tests the CLI behavior when the --url parameter is missing.
     * Verify that the awsS3UploadService.uploadFile() method was never called in the absence of --url parameter
     */
    @Test
    public void testMissingUrl() throws IOException {
        epikastLogToS3Command.run();
        verify(awsS3UploadService, never()).uploadFile(anyString());
    }

    /**
     * Test if the uploadFile method is called once when run() method is called with a valid URL.
     */
    @Test
    public void testUploadFile() throws IOException {
        String fileUrl = "http://80.90.47.7/anupam.acrylic_16.apk.diffoscope.txt";
        epikastLogToS3Command.setFileUrl(fileUrl);
        doNothing().when(awsS3UploadService).uploadFile(fileUrl);
        epikastLogToS3Command.run();
        verify(awsS3UploadService, times(1)).uploadFile(fileUrl);
    }

}
