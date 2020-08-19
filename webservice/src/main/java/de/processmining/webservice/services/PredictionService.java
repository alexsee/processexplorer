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
import com.healthmarketscience.sqlbuilder.AlterTableQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import de.processmining.data.DatabaseModel;
import de.processmining.data.prediction.EventLogResult;
import de.processmining.data.prediction.PredictionConfiguration;
import de.processmining.data.prediction.TrainingConfiguration;
import de.processmining.data.prediction.TrainingResult;
import de.processmining.webservice.database.EventLogFeatureRepository;
import de.processmining.webservice.database.EventLogModelRepository;
import de.processmining.webservice.database.EventLogRepository;
import de.processmining.webservice.database.entities.EventLogFeature;
import de.processmining.webservice.database.entities.EventLogModel;
import de.processmining.webservice.database.entities.EventLogModelState;
import de.processmining.webservice.properties.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
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

    private EventLogFeatureRepository eventLogFeatureRepository;

    private ApplicationProperties properties;

    private SimpMessagingTemplate messagingTemplate;

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public PredictionService(EventLogModelRepository eventLogModelRepository,
                             EventLogRepository eventLogRepository,
                             EventLogFeatureRepository eventLogFeatureRepository, ApplicationProperties properties, SimpMessagingTemplate messagingTemplate, JdbcTemplate jdbcTemplate) {
        this.eventLogModelRepository = eventLogModelRepository;
        this.eventLogRepository = eventLogRepository;
        this.eventLogFeatureRepository = eventLogFeatureRepository;
        this.properties = properties;
        this.messagingTemplate = messagingTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<EventLogModel> getModelsByEventLog(String logName) {
        return this.eventLogModelRepository.findByEventLogLogName(logName);
    }

    public void initCaseManagement(String logName) {
        // check if case management is already initialized
        var features = this.eventLogFeatureRepository.findByEventLogLogName(logName);

        for (var feature : features) {
            if (feature.getFeature().equals("case_management"))
                return;
        }

        // add new columns
        var db = new DatabaseModel(logName);
        var caseStateCol = db.caseTable.addColumn("state", "integer", null);
        var caseResourceCol = db.caseTable.addColumn("assigned", "varchar", 1024);
        var casePredictionsCol = db.caseTable.addColumn("prediction", "text", null);

        jdbcTemplate.execute("ALTER TABLE " + db.caseTable.getTableNameSQL() + " DROP COLUMN IF EXISTS " + caseStateCol.getColumnNameSQL());
        jdbcTemplate.execute(new AlterTableQuery(db.caseTable).setAddColumn(caseStateCol).validate().toString());

        jdbcTemplate.execute("ALTER TABLE " + db.caseTable.getTableNameSQL() + " DROP COLUMN IF EXISTS " + caseResourceCol.getColumnNameSQL());
        jdbcTemplate.execute(new AlterTableQuery(db.caseTable).setAddColumn(caseResourceCol).validate().toString());

        jdbcTemplate.execute("ALTER TABLE " + db.caseTable.getTableNameSQL() + " DROP COLUMN IF EXISTS " + casePredictionsCol.getColumnNameSQL());
        jdbcTemplate.execute(new AlterTableQuery(db.caseTable).setAddColumn(casePredictionsCol).validate().toString());

        // set all cases as closed
        jdbcTemplate.execute(new UpdateQuery(db.caseTable).addSetClause(caseStateCol, 0).validate().toString());

        // save feature
        var eventLog = eventLogRepository.findByLogName(logName);

        var feature = new EventLogFeature();
        feature.setEventLog(eventLog);
        feature.setFeature("case_management");
        feature.setValues("predictions");

        eventLogFeatureRepository.save(feature);
    }

    @Async
    public void predict(PredictionConfiguration configuration) {
        var eventLog = eventLogRepository.findByLogName(configuration.getLogName());

        // report processing
        messagingTemplate.convertAndSend("/notifications/predictions/prediction_started", eventLog);

        // call april endpoint
        try {
            var client = WebClient.builder()
                    .baseUrl(properties.getAprilBaseUri())
                    .build();

            var result = client.post().uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(configuration))
                    .retrieve()
                    .bodyToMono(EventLogResult.class)
                    .block();

            var objectMapper = new ObjectMapper();


        } catch (Exception ex) {

        }

        // report processing
        messagingTemplate.convertAndSend("/notifications/predictions/prediction_finished", eventLog);
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

            var objectMapper = new ObjectMapper();

            eventLogModel.setModelId(result.getId());
            eventLogModel.setState(EventLogModelState.TRAINED);
            eventLogModel.setTrainingDuration(result.getTrainingDuration());
            eventLogModel.setAlgorithm(result.getAlgorithm());
            eventLogModel.setHyperparameters(objectMapper.writeValueAsString(result.getHyperparameters()));
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
