package org.processexplorer.webservice.controller;

import org.processexplorer.server.analysis.mining.log.StreamingService;
import org.processexplorer.server.analysis.query.model.Event;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Alexander Seeliger on 26.08.2020.
 */
@RestController
@RequestMapping("/streaming")
public class StreamingController {

    private final StreamingService streamingService;

    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    @PostMapping
    public ResponseEntity<Void> addEvent(@RequestParam("logName") String logName, @RequestParam("caseId") String caseId, @RequestBody Event event) {
        this.streamingService.addEvent(logName, caseId, event);
        return ResponseEntity.ok().build();
    }
}
