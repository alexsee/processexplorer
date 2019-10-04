package de.tk.processmining.data.query;

import de.tk.processmining.data.query.condition.Condition;

import java.util.List;

public class CaseAttributeValueQuery {

    private String logName;

    private String attributeName;

    private List<Condition> conditions;

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
