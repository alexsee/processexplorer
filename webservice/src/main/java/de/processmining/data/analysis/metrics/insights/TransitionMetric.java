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

package de.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.dbspec.RejoinTable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Alexander Seeliger on 30.09.2019.
 */
public abstract class TransitionMetric extends CaseMetric<CaseMetric.Measure, TransitionMetric.Edge> {

    public TransitionMetric(String logName) {
        super(logName);
    }

    protected Map<Edge, CaseMetric.Measure> computeDifference(Object expr, Condition condition) {
        var sourceActivityTable = db.activityTable.rejoin("source_activity");
        var targetActivityTable = db.activityTable.rejoin("target_activity");

        var inner_sql = new SelectQuery()
                .addColumns(db.graphSourceEventCol, db.graphTargetEventCol)
                .addAliasedColumn(sourceActivityTable.findColumnByName("name"), "source_event_name")
                .addAliasedColumn(targetActivityTable.findColumnByName("name"), "target_event_name")
                .addAliasedColumn(expr, "expr")
                .addCondition(condition)
                .addJoins(SelectQuery.JoinType.INNER, db.graphVariantJoin, db.graphCaseAttributeJoin)
                .addCustomJoin(SelectQuery.JoinType.INNER, db.graphTable, sourceActivityTable, BinaryCondition.equalTo(db.graphSourceEventCol, sourceActivityTable.findColumnByName("id")))
                .addCustomJoin(SelectQuery.JoinType.INNER, db.graphTable, targetActivityTable, BinaryCondition.equalTo(db.graphTargetEventCol, targetActivityTable.findColumnByName("id")))
                .addGroupings(db.graphCaseIdCol, db.graphSourceEventCol, db.graphTargetEventCol, sourceActivityTable.findColumnByName("name"), targetActivityTable.findColumnByName("name"));

        var outer_sql = new SelectQuery()
                .addCustomColumns(new CustomSql("a.source_event"), new CustomSql("a.target_event"), new CustomSql("a.source_event_name"), new CustomSql("a.target_event_name"))
                .addAliasedColumn(FunctionCall.avg().addCustomParams(new CustomSql("a.expr")), "average")
                .addAliasedColumn(new CustomExpression("stddev(a.expr)"), "standard_deviation")
                .addAliasedColumn(FunctionCall.countAll(), "num_cases")
                .addCustomFromTable(AliasedObject.toAliasedObject(new CustomExpression(inner_sql.toString()), "a"))
                .addCustomGroupings("a.source_event", "a.target_event", "a.source_event_name", "a.target_event_name")
                .addHaving(new CustomCondition("stddev(a.expr) > 0"));

        var result = jdbcTemplate.queryForList(outer_sql.validate().toString());
        var measures = new HashMap<Edge, CaseMetric.Measure>();

        for(var item : result) {
            var edge = new Edge(item.get("source_event_name").toString(), item.get("target_event_name").toString());
            var measure = new Measure(Double.parseDouble(item.get("average").toString()), Double.parseDouble(item.get("standard_deviation").toString()));
            measure.setNumberOfCases(Long.parseLong(item.get("num_cases").toString()));

            measures.put(edge, measure);
        }

        return measures;
    }


    protected class Edge {
        private String sourceEvent;
        private String targetEvent;

        public Edge(String sourceEvent, String targetEvent) {
            this.sourceEvent = sourceEvent;
            this.targetEvent = targetEvent;
        }

        @Override
        public int hashCode() {
            return Objects.hash(sourceEvent, targetEvent);
        }

        @Override
        public boolean equals(Object obj) {
            if(obj instanceof Edge) {
                var edge2 = (Edge) obj;
                return edge2.sourceEvent.equals(this.sourceEvent) && edge2.targetEvent.equals(this.targetEvent);
            }
            return false;
        }

        public String getSourceEvent() {
            return sourceEvent;
        }

        public String getTargetEvent() {
            return targetEvent;
        }
    }

}
