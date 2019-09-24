package de.tk.processmining.data.query.condition;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import de.tk.processmining.data.DatabaseModel;

import java.util.ArrayList;
import java.util.List;

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

    public PathCondition() {}

    @Override
    public List<com.healthmarketscience.sqlbuilder.Condition> getCondition(DatabaseModel db) {
        var conditions = new ArrayList<com.healthmarketscience.sqlbuilder.Condition>();

        switch (conditionType) {
            case RESPONSE:
                conditions.add(BinaryCondition.like(db.variantsVariantCol, "%:" + start + ":%:" + end + ":%"));
                break;
            case EXISTS:
                conditions.add(BinaryCondition.like(db.variantsVariantCol, "%:" + start + ":%"));
                break;
            case START_END:
                var path = "";
                if (start != null)
                    path += ":" + start + ":";
                path += "%";
                if (end != null)
                    path += ":" + end + ":";
                conditions.add(BinaryCondition.like(db.variantsVariantCol, path));
                break;
            case CUSTOM:
            case CUSTOM_EXACT:
                conditions.add(BinaryCondition.like(db.variantsVariantCol, start));
                break;
        }

        return conditions;
    }
}
