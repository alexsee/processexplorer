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

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.InCondition;
import com.healthmarketscience.sqlbuilder.NotCondition;
import org.processexplorer.server.analysis.query.DatabaseModel;

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
