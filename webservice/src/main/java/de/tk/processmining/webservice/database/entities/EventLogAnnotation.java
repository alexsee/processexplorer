package de.tk.processmining.webservice.database.entities;

import javax.persistence.*;

@Entity
@Table(name = "_meta_event_log_annotation")
public class EventLogAnnotation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "log_name")
    private String logName;

    @Column(name = "column_name")
    private String columnName;

    @Column(name = "annotation")
    private String categorization;

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

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getCategorization() {
        return categorization;
    }

    public void setCategorization(String categorization) {
        this.categorization = categorization;
    }
}
