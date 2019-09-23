package de.tk.processmining.query;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
public class ActivityStat {

    private long eventId;

    private String eventName;

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }
}
