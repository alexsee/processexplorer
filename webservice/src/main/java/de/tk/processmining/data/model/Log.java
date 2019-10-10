package de.tk.processmining.data.model;

import java.util.List;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
public class Log {

    private String logName;

    private long numTraces;

    private long numEvents;

    private long numActivities;

    private List<String> activities;

    private List<ColumnMetaData> caseAttributes;

    private List<ColumnMetaData> eventAttributes;

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public long getNumTraces() {
        return numTraces;
    }

    public void setNumTraces(long numTraces) {
        this.numTraces = numTraces;
    }

    public long getNumEvents() {
        return numEvents;
    }

    public void setNumEvents(long numEvents) {
        this.numEvents = numEvents;
    }

    public long getNumActivities() {
        return numActivities;
    }

    public void setNumActivities(long numActivities) {
        this.numActivities = numActivities;
    }

    public List<String> getActivities() {
        return activities;
    }

    public void setActivities(List<String> activities) {
        this.activities = activities;
    }

    public List<ColumnMetaData> getCaseAttributes() {
        return caseAttributes;
    }

    public void setCaseAttributes(List<ColumnMetaData> caseAttributes) {
        this.caseAttributes = caseAttributes;
    }

    public List<ColumnMetaData> getEventAttributes() {
        return eventAttributes;
    }

    public void setEventAttributes(List<ColumnMetaData> eventAttributes) {
        this.eventAttributes = eventAttributes;
    }
}
