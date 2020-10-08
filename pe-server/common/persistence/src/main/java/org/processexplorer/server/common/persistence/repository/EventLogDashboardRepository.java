package org.processexplorer.server.common.persistence.repository;

import org.processexplorer.server.common.persistence.entity.EventLogDashboard;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Alexander Seeliger on 01.10.2020.
 */
@Repository
public interface EventLogDashboardRepository extends CrudRepository<EventLogDashboard, Long> {

    List<EventLogDashboard> findByLogName(String logName);

}
