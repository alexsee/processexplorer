package de.tk.processmining.data.query.condition;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.InCondition;
import com.healthmarketscience.sqlbuilder.NotCondition;
import de.tk.processmining.data.DatabaseModel;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
public class AttributeCondition extends Condition {

    private BinaryType binaryType;

    private String attribute;

    private Object[] values;

    public enum BinaryType {
        EQUAL_TO,
        NOT_EQUAL_TO
    }

    public AttributeCondition() {
    }

    public AttributeCondition(String attribute, BinaryType binaryType, Object[] values) {
        this.attribute = attribute;
        this.binaryType = binaryType;
        this.values = values;
    }

    @Override
    public com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db) {

        if (values == null) {
            switch (binaryType) {
                case EQUAL_TO:
                    return (new BinaryCondition(BinaryCondition.Op.EQUAL_TO, db.caseAttributeTable.addColumn("\"" + attribute + "\""), values));
                case NOT_EQUAL_TO:
                    return (new NotCondition(new BinaryCondition(BinaryCondition.Op.NOT_EQUAL_TO, db.caseAttributeTable.addColumn("\"" + attribute + "\""), values)));
            }
        } else {
            switch (binaryType) {
                case EQUAL_TO:
                    return (new InCondition(db.caseAttributeTable.addColumn("\"" + attribute + "\""), values));
                case NOT_EQUAL_TO:
                    return (new NotCondition(new InCondition(db.caseAttributeTable.addColumn("\"" + attribute + "\""), values)));
            }
        }

        return null;
    }

    public BinaryType getBinaryType() {
        return binaryType;
    }

    public void setBinaryType(BinaryType binaryType) {
        this.binaryType = binaryType;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Object[] getValues() {
        return values;
    }

    public void setValues(Object[] values) {
        this.values = values;
    }

}
