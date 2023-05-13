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

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testValidUrl() {
        assertTrue(EpikastLogToS3Command.isValidUrl("http://www.example.com/path/to/file.txt"));
        assertTrue(EpikastLogToS3Command.isValidUrl("http://www.example.com/path/to/file.log"));
        assertFalse(EpikastLogToS3Command.isValidUrl("http://www.example.com/path/to/file.jpg"));
        assertFalse(EpikastLogToS3Command.isValidUrl("http://www.example.com/path/to/"));
        assertFalse(EpikastLogToS3Command.isValidUrl("exa@mple.com"));
        assertFalse(EpikastLogToS3Command.isValidUrl("htt://example.com"));
        assertFalse(EpikastLogToS3Command.isValidUrl("example.com"));
        assertFalse(EpikastLogToS3Command.isValidUrl(" "));
        assertFalse(EpikastLogToS3Command.isValidUrl(null));
    }

    @Test
    public void testMissingUrl() throws IOException {
        epikastLogToS3Command.run();
        verify(awsS3UploadService, never()).uploadFile(anyString());
    }

    @Test
    public void testUploadFile() throws Exception {
        String fileUrl = "http://www.example.com/path/to/file.txt";
        epikastLogToS3Command.setFileUrl(fileUrl);
        doNothing().when(awsS3UploadService).uploadFile(fileUrl);
        epikastLogToS3Command.run();
        verify(awsS3UploadService, times(1)).uploadFile(fileUrl);
    }


}
