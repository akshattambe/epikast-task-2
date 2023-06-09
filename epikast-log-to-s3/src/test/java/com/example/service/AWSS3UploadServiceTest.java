package com.example.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import static org.mockito.Mockito.*;

public class AWSS3UploadServiceTest {

    private final Logger LOG = LoggerFactory.getLogger(AWSS3UploadServiceTest.class);
    private static final String URL = "http://80.90.47.7/anupam.acrylic_16.apk.diffoscope.txt";
    private static final long FILE_LENGTH = 1024;
    private static final int PART_NUMBER = 1;

    @Mock
    private S3ClientManager s3ClientManager;

    @Mock
    private AmazonS3 s3Client;

    @Mock
    private InitiateMultipartUploadResult initiateMultipartUploadResult;

    @Mock
    private UploadPartResult uploadPartResult;

    @Mock
    private InputStream inputStream;

    private AWSS3UploadService awsS3UploadService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        awsS3UploadService = new AWSS3UploadService(s3ClientManager);
        when(s3ClientManager.getS3Client()).thenReturn(s3Client);
        when(initiateMultipartUploadResult.getUploadId()).thenReturn("test-upload-id");
        when(uploadPartResult.getPartETag()).thenReturn(new PartETag(PART_NUMBER, "test-etag"));
        when(inputStream.read(any(byte[].class))).thenReturn((int) FILE_LENGTH);
        when(s3Client.initiateMultipartUpload(any(InitiateMultipartUploadRequest.class))).thenReturn(initiateMultipartUploadResult);
        when(s3Client.uploadPart(any(UploadPartRequest.class))).thenReturn(uploadPartResult);
        when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class))).thenReturn(new CompleteMultipartUploadResult());
    }

    @After
    public void teardown(){
        awsS3UploadService = null;
        s3ClientManager = null;
        s3Client = null;
        inputStream = null;
        uploadPartResult = null;
        initiateMultipartUploadResult = null;
    }


    @Test
    public void testUploadFile() throws IOException {
        awsS3UploadService.uploadFile(URL);
        verify(s3ClientManager, times(1)).getS3Client();
        verify(s3Client, never()).initiateMultipartUpload(any(InitiateMultipartUploadRequest.class));
        verify(s3Client, never()).uploadPart(any(UploadPartRequest.class));
        verify(s3Client, never()).completeMultipartUpload(any(CompleteMultipartUploadRequest.class));
        verifyNoMoreInteractions(s3Client);
    }

}

