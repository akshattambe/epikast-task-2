package com.example.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;


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
        MockitoAnnotations.openMocks(this);
        when(s3ClientManager.getS3Client()).thenReturn(amazonS3Client);
        awss3UploadService = new AWSS3UploadService(s3ClientManager);
    }

//    @Test
    public void testUploadFileToS3Bucket() throws IOException {
        //Arrange
        String bucketName = "test-bucket";
        String url = "http://80.90.47.7/anupam.acrylic_16.apk.diffoscope.txt";

        //Act
        awss3UploadService.uploadFile(url);

        //Assert
        ArgumentCaptor<PutObjectRequest> argumentCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(amazonS3Client).putObject(argumentCaptor.capture());
        PutObjectRequest actualRequest = argumentCaptor.getValue();

        assertEquals(bucketName, actualRequest.getBucketName());
    }

}

