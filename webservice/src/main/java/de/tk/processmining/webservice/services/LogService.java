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
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Seeliger on 26.09.2019.
 */
@Service
public class LogService {

    private EventLogRepository eventLogRepository;
    private QueryManager queryManager;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public LogService(EventLogRepository eventLogRepository,
                      QueryManager queryManager,
                      JdbcTemplate jdbcTemplate) {
        this.eventLogRepository = eventLogRepository;
        this.queryManager = queryManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void importLog(String fileName, String logName) {
        // read log
        var log = XLogUtils.readLog(fileName);

        // import log to database
        var log2db = new XLog2Database(jdbcTemplate, logName);
        log2db.importLog(log);

        // update database
        var eventLog = new EventLog(logName);
        eventLogRepository.save(eventLog);
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

    public void mineDFG(String logName) {
        var dfgMiner = new DirectlyFollowsGraphMiner(jdbcTemplate);
        dfgMiner.mine(logName);
    }
}
