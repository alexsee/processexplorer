package de.tk.processmining.data.query;

import de.tk.processmining.data.query.condition.Condition;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseQuery {

    private String logName;

    private List<Condition> conditions;

    public BaseQuery() {
        this.conditions = new ArrayList<>();
    }

    public BaseQuery(String logName) {
        this.logName = logName;
        this.conditions = new ArrayList<>();
    }

    public BaseQuery(String logName, List<Condition> conditions) {
        this.logName = logName;
        this.conditions = conditions;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
