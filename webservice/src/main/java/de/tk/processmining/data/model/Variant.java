package de.tk.processmining.data.model;

import java.util.Objects;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
public class Variant {

    private long id;

    private String[] path;

    private int[] pathIndex;

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

    public int[] getPathIndex() {
        return pathIndex;
    }

    public void setPathIndex(int[] pathIndex) {
        this.pathIndex = pathIndex;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variant) {
            var other = (Variant) obj;
            return other.getId() == getId();
        }
        return false;
    }
}
