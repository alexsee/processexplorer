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

import org.processexplorer.server.analysis.query.DatabaseModel;

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
