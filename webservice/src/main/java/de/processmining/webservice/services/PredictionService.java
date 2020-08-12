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

import de.processmining.data.prediction.TrainingConfiguration;
import de.processmining.data.prediction.TrainingResult;
import de.processmining.webservice.database.EventLogModelRepository;
import de.processmining.webservice.database.EventLogRepository;
import de.processmining.webservice.database.entities.EventLogModel;
import de.processmining.webservice.database.entities.EventLogModelState;
import de.processmining.webservice.properties.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.Future;

/**
 * @author Alexander Seeliger on 10.08.2020.
 */
@Service
public class PredictionService {

    private EventLogRepository eventLogRepository;

    private EventLogModelRepository eventLogModelRepository;

    private ApplicationProperties properties;

    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public PredictionService(EventLogModelRepository eventLogModelRepository,
                             EventLogRepository eventLogRepository,
                             ApplicationProperties properties, SimpMessagingTemplate messagingTemplate) {
        this.eventLogModelRepository = eventLogModelRepository;
        this.eventLogRepository = eventLogRepository;
        this.properties = properties;
        this.messagingTemplate = messagingTemplate;
    }

    public List<EventLogModel> getModelsByEventLog(String logName) {
        return this.eventLogModelRepository.findByEventLogLogName(logName);
    }

    @Async
    public Future<EventLogModel> train(TrainingConfiguration configuration) {
        var eventLog = eventLogRepository.findByLogName(configuration.getLogName());

        var eventLogModel = new EventLogModel();
        eventLogModel.setEventLog(eventLog);
        eventLogModel.setModelName(configuration.getModelName());
        eventLogModel.setCreationDate(new Timestamp(System.currentTimeMillis()));
        eventLogModel.setState(EventLogModelState.PROCESSING);
        eventLogModelRepository.save(eventLogModel);

        // report processing
        messagingTemplate.convertAndSend("/notifications/predictions/training_started", eventLogModel);

        // call april endpoint
        try {
            var client = WebClient.builder()
                    .baseUrl(properties.getAprilBaseUri())
                    .build();

            var result = client.post().uri("/train")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(configuration))
                    .retrieve()
                    .bodyToMono(TrainingResult.class)
                    .block();

            eventLogModel.setModelId(result.getId());
            eventLogModel.setState(EventLogModelState.TRAINED);
            eventLogModel.setTrainingDuration(result.getTrainingDuration());
            eventLogModel = eventLogModelRepository.save(eventLogModel);
        } catch (Exception ex) {
            eventLogModel.setState(EventLogModelState.ERROR);
            eventLogModel = eventLogModelRepository.save(eventLogModel);
        }

        // report processing
        messagingTemplate.convertAndSend("/notifications/predictions/training_finished", eventLogModel);
        return new AsyncResult<>(eventLogModel);
    }

    public Iterable<EventLogModel> getAll() {
        return eventLogModelRepository.findAll();
    }

    public void delete(long id) {
        var model = eventLogModelRepository.findById(id);
        if (model.isEmpty()) {
            return;
        }

        if (model.get().getModelId() != 0) {
            var client = WebClient.builder()
                    .baseUrl(properties.getAprilBaseUri())
                    .build();

            client.delete().uri("/models/" + model.get().getModelId())
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(Long.class)
                    .block();
        }

        eventLogModelRepository.delete(model.get());
    }
}
