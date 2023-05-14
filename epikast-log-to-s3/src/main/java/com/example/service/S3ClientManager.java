package com.example.service;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.profile.ProfilesConfigFile;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.example.EpikastLogToS3Command;
import com.example.exception.AWSProfileNotFoundException;
import io.micronaut.context.annotation.Property;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This service class is responsible for managing and providing an instance of
 * the AmazonS3 client object to other parts of the application.
 */
@Singleton
public class S3ClientManager {

    private final Logger LOG = LoggerFactory.getLogger(S3ClientManager.class);

    private AmazonS3 s3Client;

    /**
     * Constructor of S3ClientManager service class.
     *
     * @param profilePath - Path to AWS credentials from the specified profile.
     * @param profileName - Name of the AWS S3 profile.
     * @param region - AWS region where the S3 bucket is located
     */
    public S3ClientManager(@Property(name = "aws.profile.path") String profilePath,
                           @Property(name = "aws.profile.name") String profileName,
                           @Property(name = "aws.region") String region) {
        try {
            ProfilesConfigFile profilesConfigFile = new ProfilesConfigFile(profilePath);
            ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider(profilesConfigFile, profileName);

            s3Client = AmazonS3ClientBuilder
                    .standard()
                    .withCredentials(credentialsProvider)
                    .withRegion(region)
                    .build();
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
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
        try {
            s3Client.shutdown();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }
}
