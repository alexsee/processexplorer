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

import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.*;
import de.processmining.data.DatabaseModel;

/**
 * @author Alexander Seeliger on 10.12.2019.
 */
public class ReworkCondition extends Condition {

    private String activity;

    private int min;

    private int max;

    public ReworkCondition() {

    }

    public ReworkCondition(String activity, int min, int max) {
        this.activity = activity;
        this.min = min;
        this.max = max;
    }

    @Override
    public com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db) {
        var query = new SelectQuery()
                .addColumns(db.graphCaseIdCol)
                .addCondition(BinaryCondition.equalTo(db.graphSourceEventCol, activity))
                .addFromTable(db.graphTable)
                .addGroupings(db.graphCaseIdCol)
                .addHaving(ComboCondition.and(BinaryCondition.greaterThanOrEq(FunctionCall.countAll(), min), BinaryCondition.lessThanOrEq(FunctionCall.countAll(), max)));

        return new InCondition(db.caseAttributeCaseIdCol, query);
    }

    public String getActivity() {
        return this.activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getMin() {
        return this.min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return this.max;
    }

    public void setMax(int max) {
        this.max = max;
    }

}
