package com.example.service;

import com.amazonaws.SdkClientException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;

import com.example.EpikastLogToS3Command;
import io.micronaut.context.annotation.Property;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class AWSS3UploadService {
    private final String profilePath;
    private final String profileName;
    private final String region;
    private final AmazonS3 s3Client;

    private final Logger LOG = LoggerFactory.getLogger(EpikastLogToS3Command.class);

    @Value("${aws.bucket.name}")
    private String bucketName;

    private String getBucketName() {
        return bucketName;
    }

    public AWSS3UploadService(@Property(name = "aws.profile.path") String profilePath,
                              @Property(name = "aws.profile.name") String profileName,
                              @Property(name = "aws.region") String region ) {
        this.profilePath = profilePath;
        this.profileName = profileName;
        this.region = region;

        ProfilesConfigFile profilesConfigFile = new ProfilesConfigFile(profilePath);
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider(profilesConfigFile, profileName);

        s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(region)
                .build();
    }

    public void uploadFile(String url) throws IOException {

        String[] baseUrlAndFileName = extractBaseUrlAndFileName(url);

        //Initiate Multipart Upload.
        InitiateMultipartUploadResult initiateMultipartUploadResult = null;
        try {
            initiateMultipartUploadResult = s3Client.initiateMultipartUpload(new InitiateMultipartUploadRequest(getBucketName(), baseUrlAndFileName[1]));
        } catch (SdkClientException e) {
            if (initiateMultipartUploadResult != null) {
                s3Client.abortMultipartUpload(new AbortMultipartUploadRequest(getBucketName(), baseUrlAndFileName[1], initiateMultipartUploadResult.getUploadId()));
            }
            LOG.error(e.getMessage());
            throw new SdkClientException(e);
        }

        // get the part information to the list of part ETags
        List<PartETag> partETag = getPartETag(url, baseUrlAndFileName[1], initiateMultipartUploadResult, s3Client);

        //Complete multipart Upload.
        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, baseUrlAndFileName[1], initiateMultipartUploadResult.getUploadId(), partETag);
        s3Client.completeMultipartUpload(completeMultipartUploadRequest);
    }

    private List<PartETag> getPartETag (String urlString, String filename, InitiateMultipartUploadResult initiateMultipartUploadResult, AmazonS3 s3Client)  throws IOException{

        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType("text/plain");

        List<PartETag> partETags = new ArrayList<>();

        try (InputStream inputStream = httpURLConnection.getInputStream()) {
            // Get the content length of the file
            long fileLength = httpURLConnection.getContentLengthLong();
            objectMetadata.setContentLength(fileLength);



            // Define the part size, in bytes, for the multi-part upload
            long partSize = 5 * 1024 * 1024; // 5 MB

            // Upload each part of the file in a loop, until the end of the file
            long filePosition = 0;
            for (int i = 1; filePosition < fileLength; i++) {
                // Set the part size and read the next part of the file into a buffer
                long partLength = Math.min(partSize, (fileLength - filePosition));
                byte[] buffer = new byte[(int) partLength];
                inputStream.read(buffer);
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
        }
        return partETags;
    }

    private InputStream getInputStream(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        InputStream inputStream = httpURLConnection.getInputStream();

        return inputStream;
    }

    private static String[] extractBaseUrlAndFileName(String url) {
        // Get the last index of the '/' character in the URL
        int lastIndex = url.lastIndexOf("/");

        // Extract the URL and file name from the given URL
        String baseUrl = url.substring(0, lastIndex + 1); // Add 1 to include the '/'
        String fileName = url.substring(lastIndex + 1);

        // Return the base URL and file name as an array
        return new String[] { baseUrl, fileName };
    }
}
