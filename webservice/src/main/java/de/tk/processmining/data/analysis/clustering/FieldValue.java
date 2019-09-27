package de.tk.processmining.data.analysis.clustering;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
public class FieldValue {

    private String name;

    private Object value;

    public FieldValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public boolean equals(Object object) {
        if (object instanceof FieldValue) {
            FieldValue other = (FieldValue) object;

            if (!(other.getName().equals(getName()))) {
                return false;
            }

            if (other.getValue() == null && getValue() == null) {
                return true;
            }

            if (other.getValue() == null && getValue() != null || other.getValue() != null && getValue() == null) {
                return false;
            }

            return (other.getValue().equals(getValue()));
        }

        return false;
    }

    public int hashCode() {
        return (getValue() != null ? getValue().hashCode() : 0) + (getName() != null ? getName().hashCode() : 0);
    }

    @Override
    public String toString() {
        return getName() + "=" + getValue();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
