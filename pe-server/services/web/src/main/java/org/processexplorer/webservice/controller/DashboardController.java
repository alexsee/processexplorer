package org.processexplorer.webservice.controller;

import org.processexplorer.server.common.persistence.entity.EventLogDashboard;
import org.processexplorer.server.common.persistence.repository.EventLogDashboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Alexander Seeliger on 01.10.2020.
 */
@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final EventLogDashboardRepository eventLogDashboardRepository;

    @Autowired
    public DashboardController(EventLogDashboardRepository eventLogDashboardRepository) {
        this.eventLogDashboardRepository = eventLogDashboardRepository;
    }

    @GetMapping("/logName")
    public ResponseEntity<List<Long>> getDashboards(@RequestParam("logName") String logName) {
        var dashboards = eventLogDashboardRepository.findByLogName(logName);
        return ResponseEntity.ok(dashboards.stream().map(EventLogDashboard::getId).collect(Collectors.toList()));
    }

    @GetMapping()
    public ResponseEntity<EventLogDashboard> get(@RequestParam("id") long id) {
        var dashboard = eventLogDashboardRepository.findById(id);
        if (dashboard.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(dashboard.get());
    }

    @PostMapping()
    public ResponseEntity<EventLogDashboard> post(@RequestBody EventLogDashboard dashboard) {
        var eventLogDashboard = new EventLogDashboard();

        if (dashboard.getId() != 0) {
            var db = eventLogDashboardRepository.findById(dashboard.getId());
            if (db.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            eventLogDashboard = db.get();
        }

        // update dashboard
        eventLogDashboard.setContent(dashboard.getContent());
        eventLogDashboard.setLogName(dashboard.getLogName());
        eventLogDashboard.setModifiedDate(Timestamp.from(new Date().toInstant()));
        eventLogDashboard.setPage(dashboard.getPage());
        eventLogDashboard.setTitle(dashboard.getTitle());

        if (eventLogDashboard.getCreationDate() == null)
            eventLogDashboard.setCreationDate(Timestamp.from(new Date().toInstant()));

        eventLogDashboard = eventLogDashboardRepository.save(eventLogDashboard);

        return ResponseEntity.ok(eventLogDashboard);
    }
}
