package de.tk.processmining.data.query.condition;

import de.tk.processmining.data.DatabaseModel;

/**
 * @author Alexander Seeliger on 22.10.2019.
 */
public class NotCondition extends Condition {

    private Condition condition;

    public NotCondition() {
    }

    public NotCondition(Condition condition) {
        this.condition = condition;
    }

    @Override
    public com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db) {
        return new com.healthmarketscience.sqlbuilder.NotCondition(condition.getCondition(db));
    }

    public Condition getCondition() {
        return condition;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }
}
