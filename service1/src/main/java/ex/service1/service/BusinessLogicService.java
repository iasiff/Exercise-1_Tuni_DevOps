package ex.service1.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

@Service
public class BusinessLogicService {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogicService.class);

    private RestClient client;
    private Environment env;

    public BusinessLogicService(RestClient client, Environment env) {
        this.client = client;
        this.env = env;
    }


    public String statusRecord() throws Exception {

        String timestampIso = DateTimeFormatter.ISO_INSTANT.format(Instant.now());

        double uptimeHours;
        double upTimeSec;

        try (BufferedReader reader = new BufferedReader(new FileReader("/proc/uptime"))) {

            String line = reader.readLine();

            if (line != null) {

                String[] parts = line.split(" ");

                upTimeSec = Double.parseDouble(parts[0]);

            } else {

                throw new IOException("Empty /proc/uptime file");
            }
            uptimeHours = upTimeSec / (60 * 60);
        }

        File root = new File("/");

        double freeDiskMB = root.getUsableSpace() / (1024.0 * 1024.0);

        return String.format("Timestamp1-%s: uptime %.2f hours, free disk in root: %.2f Mbytes",

                timestampIso, uptimeHours, freeDiskMB);
    }


    public void writeToVStorage(String record) {

        Path baseDir = Paths.get(".").toAbsolutePath().normalize();
        Path vstorageDir = baseDir.resolve("vstorage");
        Path logFile = vstorageDir.resolve("log.txt");

        if (Files.notExists(vstorageDir)) {
            try {
                Files.createDirectories(vstorageDir);
                LOG.info("Service-1: Created directory: {}", vstorageDir);
            }
            catch (IOException e){
                LOG.debug("Service-1:",e);
            }

            if (Files.notExists(logFile)) {
                try{
                    Files.createFile(logFile);
                    LOG.info("Service-1: Created file: {}", logFile);
                }
                catch (IOException e) {
                    LOG.debug("Service-1:",e);
                }

            } else {
                LOG.info("Service-1: File already exists: {}", logFile);
            }
        }

        try{

            Files.writeString(logFile, record+"\n", StandardOpenOption.CREATE ,StandardOpenOption.APPEND);
        }
        catch (IOException e) {
            LOG.debug("Service-1:",e);
        }
    }


    public void postToStorageService(String record) {
        try {
            client.post()
                    .uri(env.getProperty("storage.url"))
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(record)
                    .retrieve()
                    .toBodilessEntity();

            System.out.println("Service1: Posted to Storage successfully.");

        } catch (ResourceAccessException | HttpClientErrorException e) {

            System.err.println("Service1: ERROR posting to Storage: " + e.getMessage());

        }
    }


    public String getService2Record(){

        return client.get()
                .uri(env.getProperty("service2.url") + "/status")
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .body(String.class);
    }

    public ResponseEntity<String> getLog() {
        try {
            return client.get()
                    .uri(env.getProperty("storage.url"))
                    .accept(MediaType.TEXT_PLAIN)
                    .retrieve()
                    .toEntity(String.class);

        } catch (ResourceAccessException | HttpClientErrorException e) {

            LOG.debug("Service1: Proxy ERROR to Storage: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error retrieving log from Storage service.");
        }
    }

}
