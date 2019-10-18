package de.tk.processmining.webservice.database;

import de.tk.processmining.webservice.database.entities.EventLogFeature;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Alexander Seeliger on 18.10.2019.
 */
public interface EventLogFeatureRepository extends CrudRepository<EventLogFeature, Long> {

    List<EventLogFeature> findByEventLogLogName(String logName);

    EventLogFeature findByEventLogLogNameAndFeature(String logName, String feature);

}
