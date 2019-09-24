package de.tk.processmining.data.model;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
public class GraphEdge {

    private String sourceEvent;
    private String targetEvent;

    private long avgDuration;
    private long minDuration;
    private long maxDuration;

    private long occurrence;

    public String getSourceEvent() {
        return sourceEvent;
    }

    public void setSourceEvent(String sourceEvent) {
        this.sourceEvent = sourceEvent;
    }

    public String getTargetEvent() {
        return targetEvent;
    }

    public void setTargetEvent(String targetEvent) {
        this.targetEvent = targetEvent;
    }

    public long getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(long avgDuration) {
        this.avgDuration = avgDuration;
    }

    public long getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(long minDuration) {
        this.minDuration = minDuration;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public long getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(long occurrence) {
        this.occurrence = occurrence;
    }
}
