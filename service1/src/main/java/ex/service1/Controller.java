package ex.service1;
import ex.service1.service.BusinessLogicService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@RestController
public class Controller {

    private static final Logger LOG = LoggerFactory.getLogger(Controller.class);

    private BusinessLogicService service;

    public Controller(BusinessLogicService service) {
        this.service = service;
    }

    @GetMapping(value = "/status", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getStatus() throws Exception {


        String service1Record = service.statusRecord();

        service.writeToVStorage(service1Record);

        service.postToStorageService(service1Record);

        String service2Record;

        try {
            service2Record = service.getService2Record();

            String combinedResponse = service1Record + "\n" + service2Record;

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(combinedResponse);

        } catch (ResourceAccessException | HttpClientErrorException e) {

            LOG.debug("Service-1: Error Calling Service2 " + e.getMessage());

            return ResponseEntity.status(500)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(" ");
        }
    }

    @GetMapping(value = "/log", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLog() {

        return service.getLog();

    }
}
