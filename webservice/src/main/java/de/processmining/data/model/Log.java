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

package de.processmining.data.model;

import java.util.List;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
public class Log {

    private String logName;

    private long numTraces;

    private long numEvents;

    private long numActivities;

    private List<Activity> activities;

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

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
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
