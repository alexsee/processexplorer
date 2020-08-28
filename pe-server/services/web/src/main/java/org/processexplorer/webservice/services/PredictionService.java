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

package org.processexplorer.webservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthmarketscience.sqlbuilder.AlterTableQuery;
import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import org.processexplorer.data.prediction.*;
import org.processexplorer.server.analysis.query.DatabaseModel;
import org.processexplorer.server.common.persistence.entity.EventLogFeature;
import org.processexplorer.server.common.persistence.entity.EventLogModel;
import org.processexplorer.server.common.persistence.entity.EventLogModelState;
import org.processexplorer.server.common.persistence.repository.EventLogFeatureRepository;
import org.processexplorer.server.common.persistence.repository.EventLogModelRepository;
import org.processexplorer.server.common.persistence.repository.EventLogRepository;
import org.processexplorer.server.common.utils.OutputBuilder;
import org.processexplorer.webservice.properties.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

    private final EventLogRepository eventLogRepository;

    private final EventLogModelRepository eventLogModelRepository;

    private final EventLogFeatureRepository eventLogFeatureRepository;

    private final ApplicationProperties properties;

    private final SimpMessagingTemplate messagingTemplate;

    private final JdbcTemplate jdbcTemplate;

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

    /**
     * Returns a list of prediction models for a given event log.
     *
     * @param logName
     * @return
     */
    public List<EventLogModel> getModelsByEventLog(String logName) {
        return this.eventLogModelRepository.findByEventLogLogName(logName);
    }

    /**
     * Initializes the case management for a given event log in order to store information about the predictions.
     *
     * @param logName
     */
    public void initCaseManagement(String logName) {
        // check if case management is already initialized
        var features = this.eventLogFeatureRepository.findByEventLogLogName(logName);

        for (var feature : features) {
            if (feature.getFeature().equals("case_management"))
                return;
        }

        // add new columns
        var db = new DatabaseModel(logName);
        var caseStateCol = db.caseAttributeTable.addColumn("state", "integer", null);
        var caseResourceCol = db.caseAttributeTable.addColumn("assigned", "varchar", 1024);
        var casePredictionsCol = db.caseAttributeTable.addColumn("prediction", "text", null);

        jdbcTemplate.execute("ALTER TABLE " + db.caseAttributeTable.getTableNameSQL() + " DROP COLUMN IF EXISTS " + caseStateCol.getColumnNameSQL());
        jdbcTemplate.execute(new AlterTableQuery(db.caseAttributeTable).setAddColumn(caseStateCol).validate().toString());

        jdbcTemplate.execute("ALTER TABLE " + db.caseAttributeTable.getTableNameSQL() + " DROP COLUMN IF EXISTS " + caseResourceCol.getColumnNameSQL());
        jdbcTemplate.execute(new AlterTableQuery(db.caseAttributeTable).setAddColumn(caseResourceCol).validate().toString());

        jdbcTemplate.execute("ALTER TABLE " + db.caseAttributeTable.getTableNameSQL() + " DROP COLUMN IF EXISTS " + casePredictionsCol.getColumnNameSQL());
        jdbcTemplate.execute(new AlterTableQuery(db.caseAttributeTable).setAddColumn(casePredictionsCol).validate().toString());

        // set all cases as closed
        jdbcTemplate.execute(new UpdateQuery(db.caseAttributeTable).addSetClause(caseStateCol, 0).validate().toString());

        // save feature
        var eventLog = eventLogRepository.findByLogName(logName);

        var feature = new EventLogFeature();
        feature.setEventLog(eventLog);
        feature.setFeature("case_management");
        feature.setValues("predictions");

        eventLogFeatureRepository.save(feature);
    }

    /**
     * Starts the prediction using the given prediction configuration. This method is non-blocking.
     *
     * @param configuration
     */
    @Async
    public void predict(PredictionConfiguration configuration) {
        var model = eventLogModelRepository.findFirstByEventLogLogNameAndUse(configuration.getLogName(), true);
        configuration.setModelId(model.getModelId());

        var eventLog = eventLogRepository.findByLogName(configuration.getLogName());
        var db = new DatabaseModel(configuration.getLogName());
        var predictionCol = db.caseAttributeTable.addColumn("prediction");

        // report processing
        messagingTemplate.convertAndSend("/notifications/predictions/prediction_started", eventLog);

        // call april endpoint
        try {
            var client = WebClient.builder()
                    .baseUrl(properties.getAprilBaseUri())
                    .build();

            var result = client.post().uri("/predict")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(configuration))
                    .exchange()
                    .flatMap(response -> response.toEntity(String.class))
                    .block();

            var objectMapper = new ObjectMapper();
            var predictions = objectMapper.readTree(result.getBody());

            var cases = predictions.findValue("cases").elements();
            while (cases.hasNext()) {
                var c = cases.next();

                var caseId = c.findValue("id").asLong();
                var attributes = c.findValue("attributes");
                var detection = attributes.findValue("detection").toString();

                var updateSQL = new UpdateQuery(db.caseAttributeTable)
                        .addSetClause(predictionCol, detection)
                        .addCondition(BinaryCondition.equalTo(db.caseAttributeCaseIdCol, caseId));

                jdbcTemplate.execute(updateSQL.validate().toString());
            }
        } catch (Exception ex) {
            logger.error("Prediction for {} event log caused exception.", configuration.getLogName(), ex);
        }

        // report processing
        messagingTemplate.convertAndSend("/notifications/predictions/prediction_finished", eventLog);
    }

    /**
     * Starts the training of a prediction model using the given training configuration. This method is async and is
     * non-blocking.
     *
     * @param configuration
     * @return
     */
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

            logger.error("Could not train model for {} event log due to exception.", configuration.getLogName(), ex);
        }

        // report processing
        messagingTemplate.convertAndSend("/notifications/predictions/training_finished", eventLogModel);
        return new AsyncResult<>(eventLogModel);
    }

    /**
     * Returns a list of all trained models.
     *
     * @return
     */
    public Iterable<EventLogModel> getAll() {
        return eventLogModelRepository.findAll();
    }

    /**
     * Delets a given model from the database and from APRIL service.
     *
     * @param id
     */
    public void delete(long id) {
        var model = eventLogModelRepository.findById(id);
        if (model.isEmpty()) {
            return;
        }

        // delete model from APRIL service
        try {
            // check if we have a model id
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
        } catch (Exception ex) {
            logger.error("Prediction model {} could not be deleted from APRIL service due to exception.", id, ex);
        }

        eventLogModelRepository.delete(model.get());
    }

    /**
     * Returns a list of all open cases for a given event log. The result contains the predictions for the next activity
     * and resource.
     *
     * @param logName
     * @return
     */
    public List<OpenCaseResult> getOpenCases(String logName) {
        var db = new DatabaseModel(logName);

        var sqlOutput = new OutputBuilder();
        sqlOutput.print("SELECT t3.*, t2.state, t2.assigned,\n" +
                        "       (SELECT a.name\n" +
                        "        FROM %s t,\n" +
                        "             %s a\n" +
                        "        WHERE t.event = a.id\n" +
                        "          AND t.case_id = t2.case_id\n" +
                        "        ORDER BY t.timestamp, t.event DESC\n" +
                        "        LIMIT 1) AS current_event,\n" +
                        "       (SELECT t.resource\n" +
                        "        FROM %s t\n" +
                        "        WHERE t.case_id = t2.case_id\n" +
                        "        ORDER BY t.timestamp, t.event DESC\n" +
                        "        LIMIT 1) AS current_resource\n" +
                        "FROM %s t2, %s t3\n" +
                        "WHERE (t2.state = 1 AND t2.prediction IS NOT NULL AND t2.case_id = t3.case_id)", db.eventTable.getTableNameSQL(),
                db.activityTable.getTableNameSQL(), db.eventTable.getTableNameSQL(), db.caseAttributeTable.getTableNameSQL(), db.caseTable.getTableNameSQL());

        return jdbcTemplate.query(sqlOutput.toString(), new OpenCaseRowMapper());
    }

    /**
     * Sets the given model as the default for predicting the next activity, resource etc. The model will be used
     * whenever a prediction is scheduled. All other models are set to non-default
     *
     * @param modelId
     * @return
     */
    public EventLogModel setDefault(long modelId) {
        var model = eventLogModelRepository.findById(modelId);
        if (model.isEmpty()) {
            return null;
        }

        var models = eventLogModelRepository.findByEventLogLogName(model.get().getModelName());
        for (var m : models) {
            m.setUse(false);
            eventLogModelRepository.save(m);
        }

        model.get().setUse(true);
        return eventLogModelRepository.save(model.get());
    }
}
