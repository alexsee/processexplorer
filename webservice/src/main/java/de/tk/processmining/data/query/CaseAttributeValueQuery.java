package de.tk.processmining.data.query;

import de.tk.processmining.data.query.condition.Condition;

import java.util.List;

public class CaseAttributeValueQuery extends BaseQuery {

    private String attributeName;

    public CaseAttributeValueQuery() {
    }

    public CaseAttributeValueQuery(String logName, String attributeName) {
        super(logName);
        this.attributeName = attributeName;
    }

    public CaseAttributeValueQuery(String logName, List<Condition> conditions, String attributeName) {
        super(logName, conditions);
        this.attributeName = attributeName;
    }


    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

}
