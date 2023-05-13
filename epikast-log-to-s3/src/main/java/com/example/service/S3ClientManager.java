package com.example.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;

@Singleton
public class S3ClientManager {
    private final AmazonS3 s3Client;

    /**
     * This service class is responsible for managing and providing an instance of
     * the AmazonS3 client object to other parts of the application.
     * @param profilePath - Path to AWS credentials from the specified profile.
     * @param profileName - Name of the AWS S3 profile.
     * @param region - AWS region where the S3 bucket is located
     */
    public S3ClientManager(@Property(name = "aws.profile.path") String profilePath,
                           @Property(name = "aws.profile.name") String profileName,
                           @Property(name = "aws.region") String region) {
        ProfilesConfigFile profilesConfigFile = new ProfilesConfigFile(profilePath);
        ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider(profilesConfigFile, profileName);

        s3Client = AmazonS3ClientBuilder
                .standard()
                .withCredentials(credentialsProvider)
                .withRegion(region)
                .build();
    }

    /**
     * Method returns the AmazonS3 client instance
     * @return AmazonS3 instance.
     */
    public AmazonS3 getS3Client() {
        return s3Client;
    }

    /**
     * Shutdown the client instance when it is no longer needed.
     */
    public void closeS3Client() {
        s3Client.shutdown();
    }
}
