package org.processexplorer.server.common.persistence.entity;

import org.hibernate.annotations.Type;

import javax.persistence.*;

/**
 * @author Alexander Seeliger on 08.12.2020.
 */
@Entity
@Table(name = "_meta_event_log_automation_action")
public class EventLogAutomationAction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "log_name")
    private String logName;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "trigger")
    private String trigger;

    @Column(name = "trigger_type")
    private String triggerType;

    @Column(name = "configuration")
    @Lob()
    @Type(type = "org.hibernate.type.TextType")
    private String configuration;

    @Column(name = "follow_up_status")
    private int followUpStatus;

    @Column(name = "active")
    private boolean active;

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

    public String getTrigger() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    public String getConfiguration() {
        return configuration;
    }

    public void setConfiguration(String configuration) {
        this.configuration = configuration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getTriggerType() {
        return triggerType;
    }

    public void setTriggerType(String triggerType) {
        this.triggerType = triggerType;
    }

    public int getFollowUpStatus() {
        return followUpStatus;
    }

    public void setFollowUpStatus(int followUpStatus) {
        this.followUpStatus = followUpStatus;
    }
}
