package com.example.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;

import com.example.EpikastLogToS3Command;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This service class provides functionality to upload a file
 * from a given URL to an Amazon S3 bucket in multiple parts.
 */
@Singleton
public class AWSS3UploadService {

    private final S3ClientManager s3ClientManager;

    private final Logger LOG = LoggerFactory.getLogger(EpikastLogToS3Command.class);

    @Value("${aws.bucket.name}")
    private String bucketName;

    private String getBucketName() {
        return bucketName;
    }

    /**
     * Constructor of AWSS3UploadService service class.
     * @param s3ClientManager - S3ClientManager service class object.
     */
    public AWSS3UploadService(S3ClientManager s3ClientManager){
        this.s3ClientManager = s3ClientManager;
    }

    /**
     * This method initiates a multipart upload for the file in the specified S3 bucket,
     * uploads each part of the file to S3, and finally completes the multipart upload.
     * @param url -  HTTP path to the publicly available .log or .txt file.
     */
    public void uploadFile(String url) throws IOException {

        String fileName = extractFileNameFromUrl(url);

        //Initiate Multipart Upload.
        InitiateMultipartUploadResult initiateMultipartUploadResult = null;
        try {
            initiateMultipartUploadResult = s3ClientManager.getS3Client().initiateMultipartUpload(new InitiateMultipartUploadRequest(getBucketName(), fileName));

            // get the part information to the list of part ETags
            List<PartETag> partETag = getPartETag(url, fileName, initiateMultipartUploadResult, s3ClientManager.getS3Client());

            //Complete multipart Upload.
            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, fileName, initiateMultipartUploadResult.getUploadId(), partETag);
            s3ClientManager.getS3Client().completeMultipartUpload(completeMultipartUploadRequest);
        } catch (SdkClientException e) {
            if (initiateMultipartUploadResult != null) {
                s3ClientManager.getS3Client().abortMultipartUpload(new AbortMultipartUploadRequest(getBucketName(), fileName, initiateMultipartUploadResult.getUploadId()));
                s3ClientManager.closeS3Client();
            }
            LOG.error(e.getMessage());
            throw new SdkClientException(e);
        }finally {
            s3ClientManager.closeS3Client();
        }

    }

    /**
     * This method takes the URL of the file, the filename, the InitiateMultipartUploadResult object, and the Amazon S3 client as input.
     * It sets up an HttpURLConnection to read the content of the file from the URL,
     * sets the ObjectMetadata for the file, sets the part size for the multipart upload, and uploads each part of the file to S3.
     * It returns a list of PartETag objects that represent the ETag of each uploaded part.
     *
     * @param urlPath - HTTP path to the publicly available .log or .txt file.
     * @param filename - Filename at the end of the urlPath.
     * @param initiateMultipartUploadResult - Contains information about the initiated multipart upload operation such as the bucket name, object key, upload ID, and other metadata.
     * @param s3Client - AmazonS3 instance.
     * @return PartETag objects that represent the ETag of each uploaded part.
     */
    public List<PartETag> getPartETag (String urlPath, String filename, InitiateMultipartUploadResult initiateMultipartUploadResult, AmazonS3 s3Client)  throws IOException{

        URL url = new URL(urlPath);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("text/plain");

        List<PartETag> partETags = new ArrayList<>();

        try{
            // Get the content length of the file
            long fileLength = httpURLConnection.getContentLengthLong();
            objectMetadata.setContentLength(fileLength);

            // Define the part size, in bytes, for the multipart upload
            long partSize = 5 * 1024 * 1024; // 5 MB

            // Upload each part of the file in a loop, until the end of the file
            long filePosition = 0;
            for (int i = 1; filePosition < fileLength; i++) {
                // Set the part size and read the next part of the file into a buffer
                long partLength = Math.min(partSize, (fileLength - filePosition));
                byte[] buffer = new byte[(int) partLength];
                filePosition += partLength;

                // Upload the part to Amazon S3
                UploadPartRequest uploadRequest = new UploadPartRequest()
                        .withBucketName(getBucketName())
                        .withKey(filename)
                        .withUploadId(initiateMultipartUploadResult.getUploadId())
                        .withPartNumber(i)
                        .withPartSize(partLength)
                        .withInputStream(new ByteArrayInputStream(buffer));
                UploadPartResult uploadResult = s3Client.uploadPart(uploadRequest);

                // Add the part information to the list of part ETags
                partETags.add(uploadResult.getPartETag());
            }
        } catch (AmazonServiceException e) {
            // handle any Amazon service exceptions here
            throw new RuntimeException("An Amazon service exception occurred while uploading file: " + e.getMessage(), e);
        } catch (AmazonClientException e) {
            // handle any Amazon client exceptions here
            throw new RuntimeException("An Amazon client exception occurred while uploading file: " + e.getMessage(), e);
        } catch (Exception e) {
            System.out.println("Unknown error occurred: " + e.getMessage());
        }

        return partETags;
    }

    /**
     * This is a utility method that takes a URL as input and returns the file name.
     * @param url - HTTP path to the publicly available .log or .txt file.
     * @return filename - Filename at the end of the urlPath.
     */
    public static String extractFileNameFromUrl(String url) {
        int lastIndex = url.lastIndexOf("/");
        return url.substring(lastIndex + 1);
    }
}
