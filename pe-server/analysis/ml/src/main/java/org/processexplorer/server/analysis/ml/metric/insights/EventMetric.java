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

package org.processexplorer.server.analysis.ml.metric.insights;

import com.healthmarketscience.sqlbuilder.*;

import java.util.HashMap;
import java.util.Map;

public abstract class EventMetric extends ClusterMetric {

    protected final String eventName;

    public EventMetric(String logName, String eventName) {
        super(logName);
        this.eventName = eventName;
    }

    protected Map<Measure, Double> computeDifference(Object expr, Condition condition) {
        var sql = new SelectQuery()
                .addAliasedColumn(FunctionCall.countAll(), "occurrence")
                .addAliasedColumn(expr, "attr")
                .addCondition(condition)
                .addJoins(SelectQuery.JoinType.INNER, db.eventCaseJoin, db.caseCaseAttributeJoin)
                .addGroupings(db.eventEventCol)
                .addCustomGroupings(expr)
                .addCondition(BinaryCondition.equalTo(db.eventEventCol, eventName));

        var result = jdbcTemplate.queryForList(sql.validate().toString());
        var measures = new HashMap<Measure, Double>();

        for (var item : result) {
            var attr = item.get("attr");

            var measure = new EventMetric.Measure(eventName, attr == null ? "" : attr.toString());
            measures.put(measure, Double.parseDouble(item.get("occurrence").toString()));
        }

        return measures;
    }
}
