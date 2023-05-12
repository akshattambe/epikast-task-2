package com.example;

import com.example.service.AWSS3UploadService;
import com.fasterxml.jackson.annotation.JsonGetter;
import io.micronaut.configuration.picocli.PicocliRunner;

import io.micronaut.core.util.StringUtils;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

import java.io.IOException;
import java.net.*;

@Command(name = "epikast-log-to-s3", description = "...",
        mixinStandardHelpOptions = true)
public class EpikastLogToS3Command implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(EpikastLogToS3Command.class);

    private AWSS3UploadService awss3UploadService;

    @Option(names = {"-u", "--url"}, description = "File url")
    private String fileUrl;

    public String setFileUrl(String url) {
        fileUrl = url;
        return fileUrl;
    }

    public EpikastLogToS3Command() {
        this.awss3UploadService = awss3UploadService;
    }

    @Inject
    public EpikastLogToS3Command(AWSS3UploadService awss3UploadService) {
        this.awss3UploadService = awss3UploadService;
    }

    public static void main(String[] args) {
        PicocliRunner.run(EpikastLogToS3Command.class, args);
    }

    public void run() {
        // business logic here
        LOG.info("Log file url: {}", fileUrl);

        if (StringUtils.isEmpty(fileUrl)) {
            System.out.println("Missing --url parameter");
        }

        if(!isValidUrl(fileUrl)){
            System.out.println("Invalid Url. Url must have path.");
        }

        HttpURLConnection httpConn = null;
        try {
            URL url = new URL(fileUrl);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.getInputStream().close();
        } catch (UnknownHostException e) {
            System.out.println("Unknown Host: " + e.getMessage());
        } catch (MalformedURLException e) {
            System.out.println("Malformed URL Host: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Exception caught: " + e.getMessage());
        }finally {
            if (httpConn != null) {
                httpConn.disconnect();
            }
        }

        try {
            awss3UploadService.uploadFile(fileUrl);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

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