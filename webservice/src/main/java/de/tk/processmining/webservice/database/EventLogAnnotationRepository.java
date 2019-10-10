package de.tk.processmining.webservice.database;

import de.tk.processmining.webservice.database.entities.EventLogAnnotation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EventLogAnnotationRepository extends CrudRepository<EventLogAnnotation, Long> {

    List<EventLogAnnotation> findByLogName(String logName);

    List<EventLogAnnotation> findByLogNameAndColumnTypeAndColumnName(String logName, String columnType, String columnName);

    void deleteAllByLogName(String logName);

}
