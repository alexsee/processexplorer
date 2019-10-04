package de.tk.processmining.data.query;

import java.util.List;

public class CaseAttributeValueResult {

    private String attributeName;

    private boolean categorical;

    private List<String> values;

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public boolean isCategorical() {
        return categorical;
    }

    public void setCategorical(boolean categorical) {
        this.categorical = categorical;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }
}
