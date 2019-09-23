package de.tk.processmining.data.model;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
public class Variant {

    private long id;

    private String[] path;

    private long occurrence;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String[] getPath() {
        return path;
    }

    public void setPath(String[] path) {
        this.path = path;
    }

    public long getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(long occurrence) {
        this.occurrence = occurrence;
    }
}
