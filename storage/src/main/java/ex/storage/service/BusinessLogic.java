package ex.storage.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;

@Service
public class BusinessLogic {

    private static final Logger LOG = LoggerFactory.getLogger(BusinessLogic.class);


    Path baseDir = Paths.get(".").toAbsolutePath().normalize();
    Path logFile = baseDir.resolve("log.txt");

    public ResponseEntity<Void> postLog(String record){

        try {

            if (Files.notExists(logFile)) {
                try{
                    Files.createFile(logFile);
                    LOG.info("Service-Storage: Created file: {}", logFile);
                }
                catch (IOException e) {
                    LOG.debug("Service-Storage:",e);
                }

            } else {
                LOG.info("Service-Storage: File already exists: {}", logFile);
            }

            Files.writeString(logFile, record+"\n",
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);

            LOG.info("Storage_Service: Appended record in Local");

            return ResponseEntity.ok().build();

        } catch (IOException e) {

            LOG.debug("Storage_Service Exception - Can not Write",e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    public ResponseEntity<String> getLog() {
        try {

            String content = Files.readString(logFile);

            LOG.info("Storage_Service: Retrieved log from local");

            return ResponseEntity.ok(content);

        } catch (NoSuchFileException e) {
            LOG.debug("Storage_Service Exception - No Log File located");
            return ResponseEntity.ok(" ");

        } catch (IOException e) {

            LOG.debug("Storage_Service Exception - IO Exception", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Error retrieving log from Storage service.");
        }
    }
}
