package com.example;

import io.micronaut.configuration.picocli.PicocliRunner;
import io.micronaut.context.ApplicationContext;

import io.micronaut.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "epikast-log-to-s3", description = "...",
        mixinStandardHelpOptions = true)
public class EpikastLogToS3Command implements Runnable {

    private final Logger LOG = LoggerFactory.getLogger(EpikastLogToS3Command.class);

    @Option(names = {"-u", "--url"}, description = "File url")
    private String fileUrl;

//    private final DownloadService downloadService;
//    private final AWSS3UploadService awss3UploadService;

    public static void main(String[] args) throws Exception {
        PicocliRunner.run(EpikastLogToS3Command.class, args);
    }

    public void run() {
        // business logic here
        LOG.info("Log file url: {}", fileUrl);

        if (StringUtils.isEmpty(fileUrl)) {
            System.out.println("Missing --url parameter");
        }
    }
}
