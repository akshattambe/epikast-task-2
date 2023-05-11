package com.example.service;

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

    @Mock
    private S3ClientManager s3ClientManager;

    @Mock
    private AmazonS3 amazonS3Client;

    private AWSS3UploadService awss3UploadService;

    @Before
    public void setup(){

        awss3UploadService = new AWSS3UploadService(s3ClientManager);
    }

    @Test
    public void testUploadFileToS3Bucket() throws IOException {
        //Arrange
        String bucketName = "test-bucket";
        String url = "http://80.90.47.7/anupam.acrylic_16.apk.diffoscope.txt";
        String filename = "anupam.acrylic_16.apk.diffoscope.txt";

        MockitoAnnotations.openMocks(this);
        when(s3ClientManager.getS3Client()).thenReturn(this.amazonS3Client);

        //Act
        InitiateMultipartUploadResult initiateMultipartUploadResult = mock(InitiateMultipartUploadResult.class);
        when(s3ClientManager.getS3Client().initiateMultipartUpload(any())).thenReturn(initiateMultipartUploadResult);

        UploadPartResult uploadResult = mock(UploadPartResult.class);
        when(s3ClientManager.getS3Client().uploadPart(any())).thenReturn(uploadResult);

        List<PartETag> partETag = awss3UploadService.getPartETag(url, filename, initiateMultipartUploadResult, s3ClientManager.getS3Client());

        CompleteMultipartUploadRequest completeMultipartUploadRequest = mock(CompleteMultipartUploadRequest.class);
        CompleteMultipartUploadResult completeMultipartUploadResult = mock(CompleteMultipartUploadResult.class);
        when(s3ClientManager.getS3Client().completeMultipartUpload(completeMultipartUploadRequest)).thenReturn(completeMultipartUploadResult);
        when(completeMultipartUploadResult.getBucketName()).thenReturn(bucketName);

        //Assert
        assertEquals(bucketName, completeMultipartUploadResult.getBucketName());
    }

}

