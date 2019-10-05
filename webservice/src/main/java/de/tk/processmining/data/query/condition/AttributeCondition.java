package de.tk.processmining.data.query.condition;

import com.healthmarketscience.sqlbuilder.InCondition;
import de.tk.processmining.data.DatabaseModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
public class AttributeCondition extends Condition {

    public void setBinaryType(BinaryType binaryType) {
        this.binaryType = binaryType;
    }

    public enum BinaryType {
        EQUAL_TO
    }

    private BinaryType binaryType;

    private String attribute;

    private Object[] values;

    @Override
    public List<com.healthmarketscience.sqlbuilder.Condition> getCondition(DatabaseModel db) {
        var conditions = new ArrayList<com.healthmarketscience.sqlbuilder.Condition>();

        switch (binaryType) {
            case EQUAL_TO:
                conditions.add(new InCondition(db.caseAttributeTable.addColumn("\"" + attribute + "\""), values));
                break;
        }

        return conditions;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }
}
