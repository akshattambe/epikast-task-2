package com.example.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class AWSS3UploadServiceTest {

    private static final String URL = "http://80.90.47.7/anupam.acrylic_16.apk.diffoscope.txt";
    private static final String BUCKET_NAME = "test-bucket";
    private static final String FILE_NAME = "anupam.acrylic_16.apk.diffoscope.txt";
    private static final long FILE_LENGTH = 1024;
    private static final int PART_NUMBER = 1;
    private static final long PART_SIZE = 5 * 1024 * 1024;

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
        MockitoAnnotations.openMocks(this);
        awsS3UploadService = new AWSS3UploadService(s3ClientManager);
        when(s3ClientManager.getS3Client()).thenReturn(s3Client);
        when(initiateMultipartUploadResult.getUploadId()).thenReturn("test-upload-id");
        when(uploadPartResult.getPartETag()).thenReturn(new PartETag(PART_NUMBER, "test-etag"));
        when(inputStream.read(any(byte[].class))).thenReturn((int) FILE_LENGTH);
        when(s3Client.initiateMultipartUpload(any(InitiateMultipartUploadRequest.class))).thenReturn(initiateMultipartUploadResult);
        when(s3Client.uploadPart(any(UploadPartRequest.class))).thenReturn(uploadPartResult);
        when(s3Client.completeMultipartUpload(any(CompleteMultipartUploadRequest.class))).thenReturn(new CompleteMultipartUploadResult());
    }

    @Test
    public void testUploadFile() throws IOException {
        awsS3UploadService.uploadFile(URL);
        verify(s3ClientManager, times(3)).getS3Client();
        verify(s3Client, times(1)).initiateMultipartUpload(any(InitiateMultipartUploadRequest.class));
        verify(s3Client, times(1)).uploadPart(any(UploadPartRequest.class));
        verify(s3Client, times(1)).completeMultipartUpload(any(CompleteMultipartUploadRequest.class));
        verifyNoMoreInteractions(s3Client);
    }

    @Test(expected = SdkClientException.class)
    public void testUploadFileWithSdkClientException() throws IOException {
        doThrow(new SdkClientException("test exception")).when(s3Client).initiateMultipartUpload(any(InitiateMultipartUploadRequest.class));
        awsS3UploadService.uploadFile(URL);
    }



//    @Mock
//    private S3ClientManager s3ClientManager;
//
//    @Mock
//    private AmazonS3 amazonS3Client;
//
//    private AWSS3UploadService awss3UploadService;
//
//    @Before
//    public void setup(){
//
//        awss3UploadService = new AWSS3UploadService(s3ClientManager);
//    }
//
//    @Test
//    public void testUploadFileToS3Bucket() throws IOException {
//        //Arrange
//        String bucketName = "test-bucket";
//        String url = "http://80.90.47.7/anupam.acrylic_16.apk.diffoscope.txt";
//        String key = "anupam.acrylic_16.apk.diffoscope.txt";
//
//        String content = "test content";
//        byte[] fileContent = content.getBytes();
//        InputStream inputStream = new ByteArrayInputStream(fileContent);
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(fileContent.length);
//
//        MockitoAnnotations.openMocks(this);
//        when(s3ClientManager.getS3Client()).thenReturn(this.amazonS3Client);
//
//        //Act
//        InitiateMultipartUploadResult initiateMultipartUploadResult = mock(InitiateMultipartUploadResult.class);
//        when(s3ClientManager.getS3Client().initiateMultipartUpload(any())).thenReturn(initiateMultipartUploadResult);
//
//        UploadPartResult uploadResult = mock(UploadPartResult.class);
//        when(s3ClientManager.getS3Client().uploadPart(any())).thenReturn(uploadResult);
//
//        List<PartETag> partETag = awss3UploadService.getPartETag(url, key, initiateMultipartUploadResult, s3ClientManager.getS3Client());
//
//        CompleteMultipartUploadRequest completeMultipartUploadRequest = mock(CompleteMultipartUploadRequest.class);
//        CompleteMultipartUploadResult completeMultipartUploadResult = mock(CompleteMultipartUploadResult.class);
//        when(s3ClientManager.getS3Client().completeMultipartUpload(completeMultipartUploadRequest)).thenReturn(completeMultipartUploadResult);
//        when(completeMultipartUploadResult.getBucketName()).thenReturn(bucketName);
//
//        //Assert
//        assertEquals(bucketName, completeMultipartUploadResult.getBucketName());
//        assertEquals(key, completeMultipartUploadResult.getKey());
//        assertEquals(metadata.getContentLength(), completeMultipartUploadResult.getMetadata().getContentLength());
//        assertEquals(inputStream, completeMultipartUploadResult.getInputStream());
//    }

}

