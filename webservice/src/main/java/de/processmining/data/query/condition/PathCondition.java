/*
 * ProcessExplorer
 * Copyright (C) 2019  Alexander Seeliger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.processmining.data.query.condition;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import de.processmining.data.DatabaseModel;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
public class PathCondition extends Condition {

    public enum ConditionType {
        RESPONSE, DIRECT_RESPONSE, NO_RESPONSE, NO_DIRECT_RESPONSE, EXISTS, START_END, NO_START_END, CUSTOM, CUSTOM_EXACT
    }

    private ConditionType conditionType;

    private Integer start;

    private Integer end;

    public PathCondition() {
    }

    @Override
    public com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db) {
        var path = "";

        switch (conditionType) {
            case RESPONSE:
                return (BinaryCondition.like(db.variantsVariantCol, "%:" + start + ":%:" + end + ":%"));
            case DIRECT_RESPONSE:
                return (BinaryCondition.like(db.variantsVariantCol, "%:" + start + "::" + end + ":%"));
            case NO_RESPONSE:
                return (BinaryCondition.notLike(db.variantsVariantCol, "%:" + start + ":%:" + end + ":%"));
            case NO_DIRECT_RESPONSE:
                return (BinaryCondition.notLike(db.variantsVariantCol, "%:" + start + "::" + end + ":%"));
            case EXISTS:
                return (BinaryCondition.like(db.variantsVariantCol, "%:" + start + ":%"));
            case START_END:
                if (start != null)
                    path += ":" + start + ":";
                path += "%";
                if (end != null)
                    path += ":" + end + ":";
                return (BinaryCondition.like(db.variantsVariantCol, path));
            case NO_START_END:
                if (start != null)
                    path += ":" + start + ":";
                path += "%";
                if (end != null)
                    path += ":" + end + ":";
                return (BinaryCondition.notLike(db.variantsVariantCol, path));
            case CUSTOM:
            case CUSTOM_EXACT:
                return (BinaryCondition.like(db.variantsVariantCol, start));
        }

        return null;
    }


    public ConditionType getConditionType() {
        return conditionType;
    }

    public void setConditionType(ConditionType conditionType) {
        this.conditionType = conditionType;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }
}
