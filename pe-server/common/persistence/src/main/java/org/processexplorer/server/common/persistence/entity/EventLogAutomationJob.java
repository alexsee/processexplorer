package org.processexplorer.server.common.persistence.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Alexander Seeliger on 08.12.2020.
 */
@Entity
@Table(name = "_meta_event_log_automation_job")
public class EventLogAutomationJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "log_name")
    private String logName;

    @Column(name = "case_id")
    private long caseId;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "configuration")
    @Lob()
    @Type(type = "org.hibernate.type.TextType")
    private String configuration;

    @Column(name = "date_created")
    private Timestamp dateCreated;

    @Column(name = "date_finished")
    private Timestamp dateFinished;

    @Column(name = "status")
    private EventLogAutomationJobStatus status;

    @Column(name = "result")
    @Lob()
    @Type(type = "org.hibernate.type.TextType")
    private String result;

    public EventLogAutomationJobStatus getStatus() {
        return status;
    }

    public void setStatus(EventLogAutomationJobStatus status) {
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
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

    public long getCaseId() {
        return caseId;
    }

    public void setCaseId(long caseId) {
        this.caseId = caseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Timestamp dateCreate) {
        this.dateCreated = dateCreate;
    }

    public Timestamp getDateFinished() {
        return dateFinished;
    }

    public void setDateFinished(Timestamp dateFinished) {
        this.dateFinished = dateFinished;
    }
}
