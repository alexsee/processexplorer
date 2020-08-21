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

package org.processexplorer.webservice.controller;

import org.processexplorer.server.analysis.query.model.Log;
import org.processexplorer.server.common.persistence.entity.EventLog;
import org.processexplorer.server.common.persistence.entity.EventLogAnnotation;
import org.processexplorer.webservice.services.LogAnnotationService;
import org.processexplorer.webservice.services.LogService;
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

    @GetMapping("/all_statistics")
    public List<Log> getAll() {
        return logService.getAllLogs();
    }

    @GetMapping()
    public Iterable<EventLog> listLogs() {
        return logService.getAll();
    }

    @PostMapping("/upload")
    public EventLog uploadLog(@RequestParam("file") MultipartFile file,
                              @RequestParam("logName") String logName) {
        // store and import log
        return logService.storeLog(file, logName);
    }

    @GetMapping("/import")
    public ResponseEntity importLog(@RequestParam("logName") String logName) {
        logService.importLog(logName);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity deleteLog(@RequestParam("logName") String logName) {
        logService.deleteLog(logName);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/annotations")
    public ResponseEntity<Iterable<EventLogAnnotation>> getAnnotations(@RequestParam("logName") String logName) {
        var result = logAnnotationService.findByLogName(logName);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/annotation")
    public ResponseEntity<EventLogAnnotation> saveAnnotation(@RequestBody EventLogAnnotation annotation) {
        var result = logAnnotationService.save(annotation);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/annotation")
    public ResponseEntity deleteAnnotation(@RequestParam("id") Long id) {
        logAnnotationService.deleteById(id);
        return ResponseEntity.ok().build();
    }

}