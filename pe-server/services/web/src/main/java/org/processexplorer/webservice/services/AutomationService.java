package org.processexplorer.webservice.services;

import org.processexplorer.server.analysis.query.DatabaseModel;
import org.processexplorer.server.common.persistence.entity.EventLogAutomationAction;
import org.processexplorer.server.common.persistence.entity.EventLogAutomationJob;
import org.processexplorer.server.common.persistence.entity.EventLogAutomationJobStatus;
import org.processexplorer.server.common.persistence.repository.EventLogAutomationActionRepository;
import org.processexplorer.server.common.persistence.repository.EventLogAutomationJobRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author Alexander Seeliger on 08.12.2020.
 */
@Service
public class AutomationService {

    private final EventLogAutomationActionRepository eventLogAutomationActionRepository;

    private final EventLogAutomationJobRepository eventLogAutomationJobRepository;

    private final JdbcTemplate jdbcTemplate;

    public AutomationService(EventLogAutomationActionRepository eventLogAutomationActionRepository, EventLogAutomationJobRepository eventLogAutomationJobRepository, JdbcTemplate jdbcTemplate) {
        this.eventLogAutomationActionRepository = eventLogAutomationActionRepository;
        this.eventLogAutomationJobRepository = eventLogAutomationJobRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EventLogAutomationAction> getAutomationActions(String logName) {
        return this.eventLogAutomationActionRepository.getEventLogAutomationActionsByLogName(logName);
    }

    public List<EventLogAutomationAction> getAutomationActions(String logName, String triggerType, String trigger) {
        return this.eventLogAutomationActionRepository.getEventLogAutomationActionsByLogNameAndTriggerTypeAndTrigger(logName, triggerType, trigger);
    }

    public EventLogAutomationJob triggerJob(String logName, Long caseId, Long actionId) {
        var db = new DatabaseModel(logName);

        // query action
        var action = eventLogAutomationActionRepository.findById(actionId).get();

        // generate new job
        var job = new EventLogAutomationJob();
        job.setName(action.getName());
        job.setLogName(logName);
        job.setType(action.getType());
        job.setConfiguration(action.getConfiguration());
        job.setCaseId(caseId);
        job.setStatus(EventLogAutomationJobStatus.SCHEDULED);
        job.setDateCreated(new Timestamp(new Date().getTime()));

        job = eventLogAutomationJobRepository.save(job);

        // update open case
        jdbcTemplate.update("UPDATE " + db.caseAttributeTable.getTableNameSQL() + " SET state = ? WHERE case_id = ?",
                action.getFollowUpStatus(), caseId);

        return job;
    }

    public List<EventLogAutomationJob> getAutomationJobs(String logName, Long caseId) {
        return eventLogAutomationJobRepository.findAllByLogNameAndCaseId(logName, caseId);
    }

}
