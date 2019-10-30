/*
 * ProcessExplorer
 * Copyright (C) 2019  Alexander Seeliger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.processmining.webservice.database;

import de.processmining.webservice.database.entities.EventLogFeature;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Alexander Seeliger on 18.10.2019.
 */
public interface EventLogFeatureRepository extends CrudRepository<EventLogFeature, Long> {

    List<EventLogFeature> findByEventLogLogName(String logName);

    EventLogFeature findByEventLogLogNameAndFeature(String logName, String feature);

}
