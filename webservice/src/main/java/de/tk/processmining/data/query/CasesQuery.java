package de.tk.processmining.data.query;

import de.tk.processmining.data.query.condition.Condition;

import java.util.List;

public class CasesQuery extends BaseQuery {

    private List<String> attributes;

    public CasesQuery() {
    }

    public CasesQuery(String logName, List<String> attributes) {
        super(logName);
        this.attributes = attributes;
    }

    public CasesQuery(String logName, List<Condition> conditions, List<String> attributes) {
        super(logName, conditions);
        this.attributes = attributes;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }
}
