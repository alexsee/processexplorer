package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.model.InsightValueFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alexander Seeliger on 01.10.2019.
 */
public class CaseAttributeMetric extends ClusterMetric {

    private final String attributeName;

    public CaseAttributeMetric(String logName, String attributeName) {
        super(logName);
        this.attributeName = attributeName;
    }

    @Override
    protected Insight generateInsight(double effectSize, List<Measure> labels, double[] with, double[] without) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);

        insight.setLabels(labels.stream().map(Measure::getAttributeValue).collect(Collectors.toList()));
        insight.setWithin(with);
        insight.setWithout(without);

        insight.setFormat(InsightValueFormat.DISTRIBUTION);
        insight.setTitle("Case Attribute");
        insight.setSubTitle(attributeName);
        return insight;
    }

    @Override
    protected Map<ClusterMetric.Measure, Double> computeDifference(Object calculation, Condition conditions) {
        var inner_sql = new SelectQuery()
                .addAliasedColumn(FunctionCall.countAll(), "occurrence")
                .addAliasedColumn(calculation, "attr")
                .addFromTable(db.caseTable)
                .addJoins(SelectQuery.JoinType.INNER, db.caseVariantJoin, db.caseCaseAttributeJoin)
                .addCondition(conditions)
                .addCustomGroupings(calculation);

        var result = jdbcTemplate.queryForList(inner_sql.validate().toString());
        var measures = new HashMap<ClusterMetric.Measure, Double>();

        for (var item : result) {
            var measure = new ClusterMetric.Measure(attributeName, item.get("attr").toString());
            measures.put(measure, Double.parseDouble(item.get("occurrence").toString()));
        }

        return measures;
    }

    @Override
    protected Object getExpression() {
        return db.caseAttributeTable.addColumn("\"" + attributeName + "\"");
    }

}
