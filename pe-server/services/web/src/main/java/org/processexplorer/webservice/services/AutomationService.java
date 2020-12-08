package org.processexplorer.webservice.services;

import org.processexplorer.server.analysis.query.DatabaseModel;
import org.processexplorer.server.common.persistence.entity.EventLogAutomationAction;
import org.processexplorer.server.common.persistence.entity.EventLogAutomationJob;
import org.processexplorer.server.common.persistence.entity.EventLogAutomationJobStatus;
import org.processexplorer.server.common.persistence.repository.EventLogAutomationActionRepository;
import org.processexplorer.server.common.persistence.repository.EventLogAutomationJobRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

/**
 * @author Alexander Seeliger on 08.12.2020.
 */
@Service
public class AutomationService {

    private final AsyncService asyncService;

    private final EventLogAutomationActionRepository eventLogAutomationActionRepository;

    private final EventLogAutomationJobRepository eventLogAutomationJobRepository;

    private final JdbcTemplate jdbcTemplate;

    public AutomationService(AsyncService asyncService, EventLogAutomationActionRepository eventLogAutomationActionRepository, EventLogAutomationJobRepository eventLogAutomationJobRepository, JdbcTemplate jdbcTemplate) {
        this.asyncService = asyncService;
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

        final EventLogAutomationJob jobResult = eventLogAutomationJobRepository.save(job);

        // update open case
        jdbcTemplate.update("UPDATE " + db.caseAttributeTable.getTableNameSQL() + " SET state = ? WHERE case_id = ?",
                action.getFollowUpStatus(), caseId);

        // trigger job
        asyncService.run(() -> this.doJob(jobResult));

        return job;
    }

    public List<EventLogAutomationJob> getAutomationJobs(String logName, Long caseId) {
        return eventLogAutomationJobRepository.findAllByLogNameAndCaseId(logName, caseId);
    }

    public void doJob(EventLogAutomationJob job) {
        if (job.getType().equals("webhook")) {
            var client = WebClient.builder()
                    .baseUrl(job.getConfiguration())
                    .build();

            try {
                var result = client.get().retrieve().bodyToMono(String.class).block();
                job.setResult(result);
                job.setStatus(EventLogAutomationJobStatus.SUCCESS);
            } catch (Exception ex) {
                job.setResult(ex.getMessage());
                job.setStatus(EventLogAutomationJobStatus.FAILURE);
            }

            job.setDateFinished(new Timestamp(new Date().getTime()));
            eventLogAutomationJobRepository.save(job);
        }
    }

}
