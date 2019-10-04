package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.*;

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
        var inner_sql = new SelectQuery()
                .addColumns(db.graphSourceEventCol, db.graphTargetEventCol)
                .addAliasedColumn(expr, "expr")
                .addCondition(condition)
                .addJoins(SelectQuery.JoinType.INNER, db.graphVariantJoin, db.graphCaseAttributeJoin)
                .addGroupings(db.graphCaseIdCol, db.graphSourceEventCol, db.graphTargetEventCol);

        var outer_sql = new SelectQuery()
                .addCustomColumns(new CustomSql("a.source_event"), new CustomSql("a.target_event"))
                .addAliasedColumn(FunctionCall.avg().addCustomParams(new CustomSql("a.expr")), "average")
                .addAliasedColumn(new CustomExpression("stddev(a.expr)"), "standard_deviation")
                .addCustomFromTable(AliasedObject.toAliasedObject(new CustomExpression(inner_sql.toString()), "a"))
                .addCustomGroupings("a.source_event", "a.target_event")
                .addHaving(new CustomCondition("stddev(a.expr) > 0"));

        var result = jdbcTemplate.queryForList(outer_sql.validate().toString());
        var measures = new HashMap<Edge, CaseMetric.Measure>();

        for(var item : result) {
            var edge = new Edge(item.get("source_event").toString(), item.get("target_event").toString());
            var measure = new Measure(Double.parseDouble(item.get("average").toString()), Double.parseDouble(item.get("standard_deviation").toString()));

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
