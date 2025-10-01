package ex.storage;


import ex.storage.service.BusinessLogic;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {


    private final BusinessLogic service;

    public Controller(BusinessLogic service) {
        this.service = service;
    }


    @PostMapping(value = "/log", consumes = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Void> postLog(@RequestBody String record) {

        return service.postLog(record);
    }


    @GetMapping(value = "/log", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getLog() {
        return service.getLog();
    }
}