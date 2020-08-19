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

package de.processmining.webservice.database.entities;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Alexander Seeliger on 10.08.2020.
 */
@Entity
@Table(name = "_meta_event_log_model")
public class EventLogModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne()
    @JoinColumn(name = "log_name", referencedColumnName = "log_name")
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "logName")
    @JsonIdentityReference(alwaysAsId = true)
    @JsonProperty("logName")
    private EventLog eventLog;

    @Column(name = "model_id")
    private long modelId;

    @Column(name = "model_name")
    private String modelName;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "algorithm")
    private String algorithm;

    @Column(name = "training_duration")
    private long trainingDuration;

    @Column(name = "hypterparameters")
    private String hyperparameters;

    @Column(name = "state")
    private EventLogModelState state;

    @Column(name = "use")
    private Boolean use;

    public EventLogModel() {

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public EventLog getEventLog() {
        return eventLog;
    }

    public void setEventLog(EventLog eventLog) {
        this.eventLog = eventLog;
    }

    public long getModelId() {
        return modelId;
    }

    public void setModelId(long modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public long getTrainingDuration() {
        return trainingDuration;
    }

    public void setTrainingDuration(long trainingDuration) {
        this.trainingDuration = trainingDuration;
    }

    public String getHyperparameters() {
        return hyperparameters;
    }

    public void setHyperparameters(String hyperparameters) {
        this.hyperparameters = hyperparameters;
    }

    public EventLogModelState getState() {
        return state;
    }

    public void setState(EventLogModelState state) {
        this.state = state;
    }

    public Boolean isUse() {
        return use;
    }

    public void setUse(Boolean use) {
        this.use = use;
    }
}
