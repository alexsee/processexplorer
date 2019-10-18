package de.tk.processmining.webservice.database.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;

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
