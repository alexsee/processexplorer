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

package org.processexplorer.server.analysis.query.condition;

import com.healthmarketscience.sqlbuilder.NotCondition;
import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import org.processexplorer.server.analysis.query.DatabaseModel;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
public class AttributeCondition extends Condition {

    private BinaryType binaryType;

    private String attribute;

    private Object[] values;

    private Long from;

    private Long to;

    public enum BinaryType {
        EQUAL_TO,
        NOT_EQUAL_TO,
        RANGE,
        INTERVAL_RANGE
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
        DbColumn column = null;

        // case duration filter
        if (attribute.equals("c_duration")) {
            column = db.caseDurationCol;
        } else if (attribute.equals("c_starttime")) {
            column = db.caseStartTimeCol;
        } else if (attribute.equals("c_endtime")) {
            column = db.caseEndTimeCol;
        } else if (attribute.equals("c_id")) {
            column = db.caseCaseIdCol;
        }

        // case attribute
        if (column == null) {
            column = db.caseAttributeTable.addColumn("\"" + attribute + "\"");
        }

        if (values.length == 1) {
            switch (binaryType) {
                case EQUAL_TO:
                    return (new BinaryCondition(BinaryCondition.Op.EQUAL_TO, column, values[0]));
                case NOT_EQUAL_TO:
                    return (new NotCondition(new BinaryCondition(BinaryCondition.Op.NOT_EQUAL_TO, column, values[0])));
            }
        } else {
            switch (binaryType) {
                case EQUAL_TO:
                    return (new InCondition(column, values));
                case NOT_EQUAL_TO:
                    return (new NotCondition(new InCondition(column, values)));
                case RANGE:
                    return new BetweenCondition(column, from, to);
                case INTERVAL_RANGE:
                    return new BetweenCondition(column, new CustomSql("interval '" + from + " days'"), new CustomSql("interval '" + to + " days'"));
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

    public Long getFrom() {
        return from;
    }

    public void setFrom(Long from) {
        this.from = from;
    }

    public Long getTo() {
        return to;
    }

    public void setTo(Long to) {
        this.to = to;
    }

}
