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

package org.processexplorer.webservice.services;

import org.processexplorer.server.common.persistence.repository.EventLogAnnotationRepository;
import org.processexplorer.server.common.persistence.entity.EventLogAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LogAnnotationService {

    private final EventLogAnnotationRepository eventLogAnnotationRepository;

    @Autowired
    public LogAnnotationService(EventLogAnnotationRepository eventLogAnnotationRepository) {
        this.eventLogAnnotationRepository = eventLogAnnotationRepository;
    }

    public Iterable<EventLogAnnotation> findByLogName(String logName) {
        return this.eventLogAnnotationRepository.findByLogName(logName);
    }

    public Iterable<EventLogAnnotation> saveAll(List<EventLogAnnotation> annotations) {
        this.eventLogAnnotationRepository.deleteAllByLogName(annotations.get(0).getLogName());
        return this.eventLogAnnotationRepository.saveAll(annotations);
    }

    public EventLogAnnotation save(EventLogAnnotation annotation) {
        return this.eventLogAnnotationRepository.save(annotation);
    }

    public void deleteById(Long id) {
        this.eventLogAnnotationRepository.deleteById(id);
    }

}
