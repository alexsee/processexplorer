package de.tk.processmining.data.query.condition;

import de.tk.processmining.data.DatabaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Seeliger on 22.10.2019.
 */
public class ComboCondition extends Condition {

    private ComboType comboType;

    private List<Condition> conditions;

    public ComboCondition() {
    }

    public ComboCondition(ComboType comboType, List<Condition> conditions) {
        this.comboType = comboType;
        this.conditions = conditions;
    }

    @Override
    public com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db) {
        var conditions = new ArrayList<com.healthmarketscience.sqlbuilder.Condition>();

        for (Condition condition : this.conditions) {
            conditions.add(condition.getCondition(db));
        }

        if (comboType == ComboType.OR) {
            return com.healthmarketscience.sqlbuilder.ComboCondition.or(conditions.toArray());
        } else {
            return com.healthmarketscience.sqlbuilder.ComboCondition.and(conditions.toArray());
        }
    }


    public ComboType getComboType() {
        return comboType;
    }

    public void setComboType(ComboType comboType) {
        this.comboType = comboType;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
