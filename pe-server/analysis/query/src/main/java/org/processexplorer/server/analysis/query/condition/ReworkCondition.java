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

import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.*;
import org.processexplorer.server.analysis.query.DatabaseModel;

/**
 * @author Alexander Seeliger on 10.12.2019.
 */
public class ReworkCondition extends Condition {

    private Integer activity;

    private int min;

    private int max;

    public ReworkCondition() {

    }

    public ReworkCondition(Integer activity, int min, int max) {
        this.activity = activity;
        this.min = min;
        this.max = max;
    }

    @Override
    public com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db) {
        var query = new SelectQuery()
                .addColumns(db.eventCaseIdCol)
                .addCondition(BinaryCondition.equalTo(db.eventEventCol, activity))
                .addFromTable(db.eventTable)
                .addGroupings(db.eventCaseIdCol)
                .addHaving(ComboCondition.and(BinaryCondition.greaterThanOrEq(FunctionCall.countAll(), min), BinaryCondition.lessThanOrEq(FunctionCall.countAll(), max)));

        return new InCondition(db.caseAttributeCaseIdCol, query);
    }

    public Integer getActivity() {
        return this.activity;
    }

    public void setActivity(Integer activity) {
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
