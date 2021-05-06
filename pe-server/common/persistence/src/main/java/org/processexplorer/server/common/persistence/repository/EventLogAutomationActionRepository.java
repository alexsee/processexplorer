package org.processexplorer.server.common.persistence.repository;

import org.processexplorer.server.common.persistence.entity.EventLogAutomationAction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexander Seeliger on 08.12.2020.
 */
@Repository
public interface EventLogAutomationActionRepository extends CrudRepository<EventLogAutomationAction, Long> {

    List<EventLogAutomationAction> getEventLogAutomationActionsByLogName(String logName);

    List<EventLogAutomationAction> getEventLogAutomationActionsByLogNameAndTrigger(String logName, String trigger);

    List<EventLogAutomationAction> getEventLogAutomationActionsByLogNameAndTriggerTypeAndTrigger(String logName,
                                                                                                 String triggerType,
                                                                                                 String trigger);


}
