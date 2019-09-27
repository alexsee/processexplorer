package de.tk.processmining.webservice.services;

import de.tk.processmining.data.XLog2Database;
import de.tk.processmining.data.XLogUtils;
import de.tk.processmining.data.analysis.DirectlyFollowsGraphMiner;
import de.tk.processmining.data.model.Log;
import de.tk.processmining.data.query.QueryManager;
import de.tk.processmining.data.storage.StorageService;
import de.tk.processmining.webservice.database.EventLogRepository;
import de.tk.processmining.webservice.database.entities.EventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Alexander Seeliger on 26.09.2019.
 */
@Service
public class LogService {

    private EventLogRepository eventLogRepository;
    private SimpMessagingTemplate messagingTemplate;
    private StorageService storageService;
    private QueryManager queryManager;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LogService(EventLogRepository eventLogRepository,
                      SimpMessagingTemplate messagingTemplate,
                      StorageService storageService,
                      QueryManager queryManager,
                      JdbcTemplate jdbcTemplate) {
        this.eventLogRepository = eventLogRepository;
        this.messagingTemplate = messagingTemplate;
        this.storageService = storageService;
        this.queryManager = queryManager;
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
        eventLogs.forEach(x -> result.add(queryManager.getLogStatistics(x.getLogName())));

        return result;
    }
}
