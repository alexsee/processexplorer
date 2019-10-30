/*
 * ProcessExplorer
 * Copyright (C) 2019  Alexander Seeliger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.processmining.webservice.controller;

import de.processmining.data.model.Log;
import de.processmining.webservice.database.entities.EventLog;
import de.processmining.webservice.database.entities.EventLogAnnotation;
import de.processmining.webservice.services.LogAnnotationService;
import de.processmining.webservice.services.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteLog(@RequestParam("logName") String logName) {
        logService.deleteLog(logName);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/annotations", method = RequestMethod.GET)
    public ResponseEntity<Iterable<EventLogAnnotation>> getAnnotations(@RequestParam("logName") String logName) {
        var result = logAnnotationService.findByLogName(logName);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/annotation", method = RequestMethod.POST)
    public ResponseEntity<EventLogAnnotation> saveAnnotation(@RequestBody EventLogAnnotation annotation) {
        var result = logAnnotationService.save(annotation);
        return ResponseEntity.ok(result);
    }

    @RequestMapping(value = "/annotation", method = RequestMethod.DELETE)
    public ResponseEntity deleteAnnotation(@RequestParam("id") Long id) {
        logAnnotationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}