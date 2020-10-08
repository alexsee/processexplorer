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

package org.processexplorer.data.prediction;

import java.sql.Timestamp;

/**
 * @author Alexander Seeliger on 19.08.2020.
 */
public class OpenCaseResult {

    private long caseId;

    private Timestamp timestampStart;

    private Timestamp timestampEnd;

    private int numEvents;

    private int numResources;

    private String assignedTo;

    private int state;

    private String currentActivity;

    private String nextActivity;

    private double nextActivityScore;

    private String currentResource;

    private String nextResource;

    private double nextResourceScore;

    private String attributes;

    public long getCaseId() {
        return caseId;
    }

    public void setCaseId(long caseId) {
        this.caseId = caseId;
    }

    public Timestamp getTimestampStart() {
        return timestampStart;
    }

    public void setTimestampStart(Timestamp timestampStart) {
        this.timestampStart = timestampStart;
    }

    public Timestamp getTimestampEnd() {
        return timestampEnd;
    }

    public void setTimestampEnd(Timestamp timestampEnd) {
        this.timestampEnd = timestampEnd;
    }

    public int getNumEvents() {
        return numEvents;
    }

    public void setNumEvents(int numEvents) {
        this.numEvents = numEvents;
    }

    public int getNumResources() {
        return numResources;
    }

    public void setNumResources(int numResources) {
        this.numResources = numResources;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCurrentActivity() {
        return currentActivity;
    }

    public void setCurrentActivity(String currentActivity) {
        this.currentActivity = currentActivity;
    }

    public String getNextActivity() {
        return nextActivity;
    }

    public void setNextActivity(String nextActivity) {
        this.nextActivity = nextActivity;
    }

    public String getCurrentResource() {
        return currentResource;
    }

    public void setCurrentResource(String currentResource) {
        this.currentResource = currentResource;
    }

    public String getNextResource() {
        return nextResource;
    }

    public void setNextResource(String nextResource) {
        this.nextResource = nextResource;
    }

    public String getAttributes() {
        return attributes;
    }

    public void setAttributes(String attributes) {
        this.attributes = attributes;
    }

    public double getNextActivityScore() {
        return nextActivityScore;
    }

    public void setNextActivityScore(double nextActivityScore) {
        this.nextActivityScore = nextActivityScore;
    }

    public double getNextResourceScore() {
        return nextResourceScore;
    }

    public void setNextResourceScore(double nextResourceScore) {
        this.nextResourceScore = nextResourceScore;
    }
}
