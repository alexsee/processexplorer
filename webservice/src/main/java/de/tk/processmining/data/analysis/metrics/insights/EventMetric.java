package de.tk.processmining.data.analysis.metrics.insights;

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
                .addJoins(SelectQuery.JoinType.INNER, db.eventCaseJoin, db.caseVariantJoin, db.caseCaseAttributeJoin)
                .addGroupings(db.eventEventNameCol)
                .addCustomGroupings(expr)
                .addCondition(BinaryCondition.equalTo(db.eventEventNameCol, eventName));

        var result = jdbcTemplate.queryForList(sql.validate().toString());
        var measures = new HashMap<Measure, Double>();

        for (var item : result) {
            var measure = new EventMetric.Measure(eventName, item.get("attr").toString());
            measures.put(measure, Double.parseDouble(item.get("occurrence").toString()));
        }

        return measures;
    }
}
