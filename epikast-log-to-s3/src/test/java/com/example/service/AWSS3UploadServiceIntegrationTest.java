package com.example.service;


import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;

import java.net.HttpURLConnection;

public class AWSS3UploadServiceIntegrationTest {

//    public void testFileExists() {
//        String fileName = AWSS3UploadService.extractFileNameFromUrl("http://80.90.47.7/anupam.acrylic_16.apk.diffoscope.txt");
//        String md5 = "b30da6bd8e6a1e46f39635eb2d225f7c";
//        try {
//            S3ClientManager s3ClientManager1 = new S3ClientManager(getProfilePath(), getProfileName(), getRegion());
//            ObjectMetadata objectMetadata = s3ClientManager1.getS3Client().getObjectMetadata(getBucketName(), fileName);
//            String test = objectMetadata.getContentMD5();
////            return true;
//        } catch (AmazonS3Exception e) {
//            if (e.getStatusCode() == HttpURLConnection.HTTP_NOT_FOUND) {
////                return false;
//            }
//            LOG.error("Error checking file {} from bucket {}: {}", fileName, getBucketName(), e.getMessage());
//            throw e;
//        } finally {
//            s3ClientManager.closeS3Client();
//        }
//    }
}
