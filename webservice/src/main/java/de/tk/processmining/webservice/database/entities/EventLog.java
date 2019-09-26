package de.tk.processmining.webservice.database.entities;

import javax.persistence.*;
import java.sql.Timestamp;
import java.time.Instant;

/**
 * @author Alexander Seeliger on 26.09.2019.
 */
@Entity
@Table(name = "_meta_event_log")
public class EventLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "log_name")
    private String logName;

    @Column(name = "creation_date")
    private Timestamp creationDate;

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
}
