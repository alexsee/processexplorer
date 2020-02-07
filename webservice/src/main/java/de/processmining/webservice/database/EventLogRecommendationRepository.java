/*
 * ProcessExplorer
 * Copyright (C) 2020  Alexander Seeliger
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

import de.processmining.webservice.database.entities.EventLogRecommendation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author Alexander Seeliger on 07.02.2020.
 */
public interface EventLogRecommendationRepository extends CrudRepository<EventLogRecommendation, Long> {

    List<EventLogRecommendation> findByEventLogLogName(String logName);

}
