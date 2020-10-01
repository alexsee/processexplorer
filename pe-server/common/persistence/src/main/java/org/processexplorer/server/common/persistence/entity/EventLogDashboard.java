package org.processexplorer.server.common.persistence.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Alexander Seeliger on 01.10.2020.
 */
@Entity
@Table(name = "_meta_event_log_dashboard")
public class EventLogDashboard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "log_name")
    private String logName;

    @Column(name = "title")
    private String title;

    @Column(name = "page")
    private int page;

    @Column(name = "creation_date")
    private Timestamp creationDate;

    @Column(name = "modified_date")
    private Timestamp modifiedDate;

    @Column(name = "content")
    @Lob()
    @Type(type = "org.hibernate.type.TextType")
    private String content;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public Timestamp getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Timestamp creationDate) {
        this.creationDate = creationDate;
    }

    public Timestamp getModifiedDate() {
        return modifiedDate;
    }

    public void setModifiedDate(Timestamp modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
