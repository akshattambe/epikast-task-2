package com.example.service;

import com.amazonaws.services.s3.AmazonS3;
import org.junit.Before;

import static org.mockito.Mockito.mock;

public class AWSS3UploadServiceTest {

    AmazonS3 s3ClientMock;
    private AWSS3UploadService awss3UploadService;

//    @Before
    public void setup(){
        s3ClientMock = mock(AmazonS3.class);
    }

}
