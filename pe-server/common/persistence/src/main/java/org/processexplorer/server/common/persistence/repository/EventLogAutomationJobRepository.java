package org.processexplorer.server.common.persistence.repository;

import org.processexplorer.server.common.persistence.entity.EventLogAutomationJob;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexander Seeliger on 08.12.2020.
 */
@Repository
public interface EventLogAutomationJobRepository extends CrudRepository<EventLogAutomationJob, Long> {

    List<EventLogAutomationJob> findAllByLogNameAndCaseId(String logName, Long caseId);

    List<EventLogAutomationJob> findAllByLogName(String logName);
}
