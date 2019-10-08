package de.tk.processmining.webservice.controller;

import de.tk.processmining.data.model.Log;
import de.tk.processmining.webservice.database.entities.EventLog;
import de.tk.processmining.webservice.database.entities.EventLogAnnotation;
import de.tk.processmining.webservice.services.LogAnnotationService;
import de.tk.processmining.webservice.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController()
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;
    private final LogAnnotationService logAnnotationService;

    @Autowired
    public LogController(LogService logService, LogAnnotationService logAnnotationService) {
        this.logService = logService;
        this.logAnnotationService = logAnnotationService;
    }

    @RequestMapping("/all_statistics")
    public List<Log> getAll() {
        return logService.getAllLogs();
    }

    @RequestMapping()
    public Iterable<EventLog> listLogs() {
        return logService.getAll();
    }

    @RequestMapping("/upload")
    public EventLog uploadLog(@RequestParam("file") MultipartFile file,
                              @RequestParam("logName") String logName) {
        // store and import log
        return logService.storeLog(file, logName);
    }

    @RequestMapping("/import")
    public ResponseEntity importLog(@RequestParam("logName") String logName) {
        var result = logService.importLog(logName);
        return ResponseEntity.ok().build();
    }

    @RequestMapping("/process")
    public ResponseEntity processLog(@RequestParam("logName") String logName) {
        // generate directly follows graph
        var result = logService.processLog(logName);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/annotations", method = RequestMethod.GET)
    public ResponseEntity<Iterable<EventLogAnnotation>> getAnnotations(@RequestParam("logName") String logName) {
        var result = logAnnotationService.findByLogName(logName);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/annotations", method = RequestMethod.POST)
    public ResponseEntity<Iterable<EventLogAnnotation>> saveAnnotations(@RequestParam("annotations") List<EventLogAnnotation> annotations) {
        var result = logAnnotationService.saveAll(annotations);
        return ResponseEntity.ok(result);
    }

}