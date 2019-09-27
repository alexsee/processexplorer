package de.tk.processmining.webservice.database;

import de.tk.processmining.webservice.database.entities.EventLog;
import org.springframework.data.repository.CrudRepository;

/**
 * @author Alexander Seeliger on 26.09.2019.
 */
public interface EventLogRepository extends CrudRepository<EventLog, Long> {

    EventLog findByLogName(String logName);
}
