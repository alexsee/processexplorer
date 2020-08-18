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

package de.processmining.webservice.database.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * @author Alexander Seeliger on 26.09.2019.
 */
@Entity
@Table(name = "_meta_event_log")
public class EventLog implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "log_name")
    private String logName;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "type")
    private String type;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "imported")
    private boolean imported;

    @Column(name = "processed")
    private boolean processed;

    @Column(name = "processing")
    private boolean processing;

    @Column(name = "error_message")
    @Lob()
    @Type(type = "org.hibernate.type.TextType")
    private String errorMessage;

    @OneToMany(mappedBy = "eventLog", targetEntity = EventLogFeature.class, fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<EventLogFeature> features;

    protected EventLog() {
    }

    public EventLog(String logName) {
        this.logName = logName;
        this.creationDate = Timestamp.from(Instant.now());
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isImported() {
        return imported;
    }

    public void setImported(boolean imported) {
        this.imported = imported;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public boolean isProcessing() {
        return processing;
    }

    public void setProcessing(boolean processing) {
        this.processing = processing;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public List<EventLogFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<EventLogFeature> features) {
        this.features = features;
    }

}
