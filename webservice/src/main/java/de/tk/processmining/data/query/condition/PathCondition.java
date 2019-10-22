package de.tk.processmining.data.query.condition;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import de.tk.processmining.data.DatabaseModel;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
public class PathCondition extends Condition {

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public enum ConditionType {
        RESPONSE, EXISTS, START_END, CUSTOM, CUSTOM_EXACT
    }

    private ConditionType conditionType;

    private String start;

    private String end;

    public PathCondition() {
    }

    @Override
    public com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db) {

        switch (conditionType) {
            case RESPONSE:
                return (BinaryCondition.like(db.variantsVariantCol, "%:" + start + ":%:" + end + ":%"));
            case EXISTS:
                return (BinaryCondition.like(db.variantsVariantCol, "%:" + start + ":%"));
            case START_END:
                var path = "";
                if (start != null)
                    path += ":" + start + ":";
                path += "%";
                if (end != null)
                    path += ":" + end + ":";
                return (BinaryCondition.like(db.variantsVariantCol, path));
            case CUSTOM:
            case CUSTOM_EXACT:
                return (BinaryCondition.like(db.variantsVariantCol, start));
        }

        return null;
    }
}
