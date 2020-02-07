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

import com.fasterxml.jackson.annotation.JsonBackReference;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Alexander Seeliger on 07.02.2020.
 */
@Entity
@Table(name = "_meta_event_log_recommendation")
public class EventLogRecommendation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @ManyToOne()
    @JoinColumn(name = "log_name", referencedColumnName = "log_name")
    @JsonBackReference
    private EventLog eventLog;

    @Column(name = "score")
    private Double score;

    @Column(name = "num_traces")
    private Long numTraces;

    @Column(name = "conditions")
    @Lob()
    @Type(type = "org.hibernate.type.TextType")
    private String conditions;

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

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Long getNumTraces() {
        return numTraces;
    }

    public void setNumTraces(Long numTraces) {
        this.numTraces = numTraces;
    }

    public String getConditions() {
        return conditions;
    }

    public void setConditions(String conditions) {
        this.conditions = conditions;
    }
}
