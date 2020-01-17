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

package de.processmining.webservice.services;

import de.processmining.data.DatabaseModel;
import de.processmining.data.XLog2Database;
import de.processmining.data.XLogUtils;
import de.processmining.data.analysis.DirectlyFollowsGraphMiner;
import de.processmining.data.model.Log;
import de.processmining.data.query.QueryService;
import de.processmining.data.storage.StorageService;
import de.processmining.webservice.database.EventLogAnnotationRepository;
import de.processmining.webservice.database.EventLogArtifactRepository;
import de.processmining.webservice.database.EventLogFeatureRepository;
import de.processmining.webservice.database.EventLogRepository;
import de.processmining.webservice.database.entities.EventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Alexander Seeliger on 26.09.2019.
 */
@Service
public class LogService {

    private EventLogRepository eventLogRepository;
    private EventLogFeatureRepository eventLogFeatureRepository;
    private EventLogArtifactRepository eventLogArtifactRepository;
    private EventLogAnnotationRepository eventLogAnnotationRepository;

    private SimpMessagingTemplate messagingTemplate;
    private StorageService storageService;
    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LogService(EventLogRepository eventLogRepository,
                      EventLogFeatureRepository eventLogFeatureRepository,
                      EventLogArtifactRepository eventLogArtifactRepository,
                      EventLogAnnotationRepository eventLogAnnotationRepository,
                      SimpMessagingTemplate messagingTemplate,
                      StorageService storageService,
                      QueryService queryService,
                      JdbcTemplate jdbcTemplate) {
        this.eventLogRepository = eventLogRepository;
        this.eventLogFeatureRepository = eventLogFeatureRepository;
        this.eventLogArtifactRepository = eventLogArtifactRepository;
        this.eventLogAnnotationRepository = eventLogAnnotationRepository;
        this.messagingTemplate = messagingTemplate;
        this.storageService = storageService;
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Stores an event log file to the system in a temporary location for import.
     *
     * @param file
     * @param logName
     * @return
     */
    public EventLog storeLog(MultipartFile file, String logName) {
        storageService.store(file);

        var eventLog = new EventLog(logName);
        eventLog.setFileName(file.getOriginalFilename());
        eventLog.setImported(false);
        eventLog.setProcessed(false);
        eventLog.setType((file.getOriginalFilename().endsWith(".xes") || file.getOriginalFilename().endsWith(".xes.gz")) ? "xes" : "csv");
        eventLog = eventLogRepository.save(eventLog);

        // report finished process
        messagingTemplate.convertAndSend("/notifications/logs/stored", eventLog);
        return eventLog;
    }

    /**
     * Imports an event log to the database.
     *
     * @param logName
     * @return
     */
    @Async
    public Future<EventLog> importLog(String logName) {
        // get event log
        var eventLog = eventLogRepository.findByLogName(logName);
        eventLog.setProcessing(true);
        eventLog = eventLogRepository.save(eventLog);

        // report processing
        messagingTemplate.convertAndSend("/notifications/logs/import_started", eventLog);

        // load file from storage service
        var fileName = storageService.load(eventLog.getFileName()).toFile().getAbsolutePath();

        // read log
        if (eventLog.getType().equals("xes")) {
            var log = XLogUtils.readLog(fileName);

            // import log to database
            var log2db = new XLog2Database(jdbcTemplate, logName);
            log2db.importLog(log);

            // update database
            eventLog.setImported(true);

            // delete file
            //            storageService.delete(eventLog.getFileName());
        } else {
            eventLog.setImported(false);
            eventLog.setErrorMessage("Not supported");
        }

        eventLog.setProcessing(false);
        eventLog = eventLogRepository.save(eventLog);

        // report finished process
        messagingTemplate.convertAndSend("/notifications/logs/import_finished", eventLog);
        return new AsyncResult<>(eventLog);
    }

    /**
     * Computes a directly follows graph for the given event log.
     *
     * @param logName
     * @return
     */
    @Async
    public Future<EventLog> processLog(String logName) {
        // get event log
        var eventLog = eventLogRepository.findByLogName(logName);
        eventLog.setProcessing(true);
        eventLog = eventLogRepository.save(eventLog);

        // report processing
        messagingTemplate.convertAndSend("/notifications/logs/processing_started", eventLog);

        // perform dfg miner
        var dfgMiner = new DirectlyFollowsGraphMiner(jdbcTemplate);
        dfgMiner.mine(logName);

        // finalize result
        eventLog.setProcessed(true);
        eventLog.setProcessing(false);
        eventLog = eventLogRepository.save(eventLog);

        // report finished process
        messagingTemplate.convertAndSend("/notifications/logs/processing_finished", eventLog);
        return new AsyncResult<>(eventLog);
    }

    /**
     * Returns all imported event logs.
     *
     * @return
     */
    public Iterable<EventLog> getAll() {
        return eventLogRepository.findAll();
    }

    /**
     * Get all available and imported event logs.
     *
     * @return
     */
    public List<Log> getAllLogs() {
        var eventLogs = eventLogRepository.findAll();
        var result = new ArrayList<Log>();
        eventLogs.forEach(x -> result.add(queryService.getLogStatistics(x.getLogName())));

        return result;
    }

    /**
     * Deletes all data tables from the database and removes all entries from the database.
     *
     * @param logName
     */
    @Transactional
    public void deleteLog(String logName) {
        var eventLog = eventLogRepository.findByLogName(logName);

        if (eventLog == null) {
            return;
        }

        // remove metadata
        eventLogAnnotationRepository.deleteAllByLogName(logName);
        eventLogArtifactRepository.deleteAllByLogName(logName);
        eventLogFeatureRepository.deleteAllByEventLogLogName(logName);
        eventLogRepository.delete(eventLog);

        // remove data tables
        var db = new DatabaseModel(logName);
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + db.caseTable.getTableNameSQL());
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + db.variantsTable.getTableNameSQL());
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + db.caseAttributeTable.getTableNameSQL());
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + db.graphTable.getTableNameSQL());
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + db.activityTable.getTableNameSQL());
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + db.eventTable.getTableNameSQL());

        // report finished deletion
        messagingTemplate.convertAndSend("/notifications/logs/deleted", logName);
    }
}
