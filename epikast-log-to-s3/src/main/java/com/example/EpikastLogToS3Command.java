package com.example;

import com.example.exception.AWSProfileNotFoundException;
import com.example.service.AWSS3UploadService;
import io.micronaut.configuration.picocli.PicocliRunner;

import io.micronaut.core.util.StringUtils;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.net.*;

/**
 * EpikastLogToS3Command class defines a command-line interface (CLI) application
 * to upload log files to Amazon S3.
 * The class uses the picocli library to parse command-line arguments and options,
 * and the AWSS3UploadService class to upload the log files to Amazon S3.
 */
@Command(name = "epikast-log-to-s3", description = "...",
        mixinStandardHelpOptions = true)
public class EpikastLogToS3Command implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(EpikastLogToS3Command.class);

    private final AWSS3UploadService awss3UploadService;

    /**
     * Defines a field called fileUrl.
     * The @Option annotation is from the Picocli library and
     * is used to declare a command line option for a CLI application.
     * @Option annotation is from the Picocli library and is used to declare a command line option for a CLI application.
     */
    @Option(names = {"-u", "--url"}, description = "File url")
    private String fileUrl;

    /**
     * Setter method to set the value of the fileUrl field.
     * It returns the newly set fileUrl value. This is used in unit test.
     * @param url - HTTP path to the publicly available .log or .txt file.
     */
    public void setFileUrl(String url) {
        fileUrl = url;
    }

    /**
     * Argument constructor. It sets the value of the Upload service object.
     * @param awss3UploadService - Instance of AWSS3UploadService class.
     */
    @Inject
    public EpikastLogToS3Command(AWSS3UploadService awss3UploadService) {
            this.awss3UploadService = awss3UploadService;
    }

    /**
     * Entry point for the CLI application.
     * @param args - CLI arguments. A valid arg would be url path to a .txt ot a .log file.
     */
    public static void main(String[] args) {
        // Parse the command-line arguments and options and run the application.
        try {
            PicocliRunner.run(EpikastLogToS3Command.class, args);
        } catch (Exception e) {
            // Handle the exception
            System.out.println("AWSProfileNotFoundException caught: " + e.getMessage());
        }
    }

    /**
     * 1. Implements the business logic of the application.
     * 2. It also verifies if the url provided is valid and
     * 3. Return back to use with validation message if url is not correct.
     */
    public void run() {
        // business logic here.
        // Check for valid url string.
        if(!isValidUrl(fileUrl)){
            System.out.println("Invalid Url: " + fileUrl + "\nUrl must have a HTTP path to the publicly available .log or .txt file.");
            return;
        }

        // Calling Upload file service.
        try {
            if(awss3UploadService.isS3ClientNull()){
                return;
            }
            LOG.info("Log file url: {}", fileUrl);
            awss3UploadService.uploadFile(fileUrl);
        } catch (IOException e) {
            LOG.error(e.getMessage());
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
    }

    /**
     * Method to check if a URL string is valid.
     * It checks if the URL has a non-empty and a valid path with a .txt or .log file.
     * @param urlPath - HTTP path to the publicly available .log or .txt file.
     * @return boolean
     */
    public boolean isValidUrl(String urlPath) {
        try {

            // Check for empty path or null.
            if (StringUtils.isEmpty(urlPath) || (urlPath == null)) {
                LOG.error("Missing --url parameter");
                return false;
            }

            // Check for possible exceptions.
            HttpURLConnection httpConn = null;
            try {
                URL url = new URL(urlPath);
                httpConn = (HttpURLConnection) url.openConnection();
                httpConn.getInputStream().close();
            } catch (UnknownHostException e) {
                LOG.error(e.getMessage());
                return false;
            } catch (MalformedURLException e) {
                LOG.error(e.getMessage());
                return false;
            } catch (IOException e) {
                LOG.error(e.getMessage());
                return false;
            } finally {
                if (httpConn != null) {
                    httpConn.disconnect();
                }
            }
            return (urlPath.contains(".txt") || urlPath.contains(".log"));
        }
        catch (Exception e) {
            LOG.error(e.getMessage());
            return false;
        }
    }

}