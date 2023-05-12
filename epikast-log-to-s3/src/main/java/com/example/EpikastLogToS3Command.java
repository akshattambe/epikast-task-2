package com.example;

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
 *
 * The class uses the picocli library to parse command-line arguments and options,
 * and the AWSS3UploadService class to upload the log files to Amazon S3.
 */
@Command(name = "epikast-log-to-s3", description = "...",
        mixinStandardHelpOptions = true)
public class EpikastLogToS3Command implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(EpikastLogToS3Command.class);

    /**
     *
     */
    private AWSS3UploadService awss3UploadService;

    /**
     * Defines a field called fileUrl.
     * The @Option annotation is from the Picocli library and
     * is used to declare a command line option for a CLI application.
     *
     * @Option annotation is from the Picocli library and
     * is used to declare a command line option for a CLI application.
     *
     * @param names = {"-u", "--url"} : means that the user can pass either -u <value> or --url=<value>
     *              as a command line argument to set the value of fileUrl.
     *
     * @param description = description = "File url" : provides a description of the option,
     *                    which will be displayed in the command line help message
     *
     */
    @Option(names = {"-u", "--url"}, description = "File url")
    private String fileUrl;

    /**
     * Setter method to set the value of the fileUrl field.
     * It returns the newly set fileUrl value. This is used in unit test.
     * @param url
     * @return fileUrl
     */
    public String setFileUrl(String url) {
        fileUrl = url;
        return fileUrl;
    }

    /**
     * Default constructor.
     */
    public EpikastLogToS3Command() {
        this.awss3UploadService = awss3UploadService;
    }

    /**
     * Argument constructor. It sets the value of the Upload service object.
     * @param awss3UploadService
     */
    @Inject
    public EpikastLogToS3Command(AWSS3UploadService awss3UploadService) {
        this.awss3UploadService = awss3UploadService;
    }

    /**
     * Entry point for the CLI application.
     * @param args
     */
    public static void main(String[] args) {
        // Parse the command-line arguments and options and run the application.
        PicocliRunner.run(EpikastLogToS3Command.class, args);
    }

    /**
     * 1. Implements the business logic of the application.
     * 2. It also verifies if the url provided is valid and
     * 3. Return back to use with validation message if url is not correct.
     */
    public void run() {
        // business logic here
        LOG.info("Log file url: {}", fileUrl);

        // Check of empty string.
        if (StringUtils.isEmpty(fileUrl)) {
            System.out.println("Missing --url parameter");
        }

        // Check for valid url string.
        if(!isValidUrl(fileUrl)){
            System.out.println("Invalid Url. Url must have path.");
        }

        // Check for possible exceptions.
        HttpURLConnection httpConn = null;
        try {
            URL url = new URL(fileUrl);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.getInputStream().close();
        } catch (UnknownHostException e) {
            System.out.println("Unknown Host: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception caught: " + e.getMessage());
        }finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }

        // Calling Upload file service.
        try {
            awss3UploadService.uploadFile(fileUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Method to check if a URL string is valid.
     * It checks if the URL has a non-empty path and if the path ends with .txt or .log.
     * @param urlString
     * @return boolean
     */
    public static boolean isValidUrl(String urlString) {
        try {
            URL url = new URL(urlString);
            String path = url.getPath();
            return !path.isEmpty() && (path.contains(".txt") || path.contains(".log"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}