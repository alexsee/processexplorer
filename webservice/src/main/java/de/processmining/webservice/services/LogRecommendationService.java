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

package de.processmining.webservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.processmining.data.analysis.recommender.RecommendationService;
import de.processmining.data.model.Recommendation;
import de.processmining.data.query.ConditionList;
import de.processmining.webservice.database.EventLogRecommendationRepository;
import de.processmining.webservice.database.EventLogRepository;
import de.processmining.webservice.database.entities.EventLogRecommendation;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Seeliger on 07.02.2020.
 */
@Service
public class LogRecommendationService {

    private EventLogRepository eventLogRepository;
    private EventLogRecommendationRepository eventLogRecommendationRepository;
    private RecommendationService recommendationService;

    public LogRecommendationService(
            EventLogRepository eventLogRepository,
            EventLogRecommendationRepository eventLogRecommendationRepository,
            RecommendationService recommendationService) {
        this.eventLogRepository = eventLogRepository;
        this.eventLogRecommendationRepository = eventLogRecommendationRepository;
        this.recommendationService = recommendationService;
    }

    public List<Recommendation> getRecommendations(String logName) {
        // read cached recommendations
        var eventLog = eventLogRepository.findByLogName(logName);
        var cachedRecommendations = eventLogRecommendationRepository.findByEventLogLogName(logName);

        if (!cachedRecommendations.isEmpty()) {
            var objectMapper = new ObjectMapper();
            var result = new ArrayList<Recommendation>();

            for (var recommendation : cachedRecommendations) {
                try {
                    var rec = new Recommendation();
                    rec.setScore(recommendation.getScore());
                    rec.setNumTraces(recommendation.getNumTraces());

                    rec.setConditions(objectMapper.readValue(recommendation.getConditions(), ConditionList.class));
                    result.add(rec);
                } catch (Exception ex) {
                    // could not deserialize conditions
                }
            }

            return result;
        }

        // no cached result, so query (takes some time)
        var computedRecommendations = recommendationService.getRecommendations(logName);

        for (var recommendation : computedRecommendations) {
            try {
                var eventLogRecommendation = new EventLogRecommendation();
                eventLogRecommendation.setEventLog(eventLog);
                eventLogRecommendation.setNumTraces(recommendation.getNumTraces());
                eventLogRecommendation.setScore(recommendation.getScore());

                var objectMapper = new ObjectMapper();
                var list = new ConditionList();
                list.addAll(recommendation.getConditions());

                eventLogRecommendation.setConditions(objectMapper.writeValueAsString(list));

                eventLogRecommendationRepository.save(eventLogRecommendation);
            } catch (Exception ex) {
                // could not serialize conditions
            }
        }

        return computedRecommendations;
    }
}
