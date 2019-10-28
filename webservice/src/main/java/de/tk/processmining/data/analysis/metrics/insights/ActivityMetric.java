package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import de.tk.processmining.data.analysis.categorization.AnalysisTargetCodes;
import de.tk.processmining.data.analysis.categorization.DomainCodes;
import de.tk.processmining.data.analysis.categorization.EventAttributeCodes;
import de.tk.processmining.data.analysis.categorization.VisualizationCodes;
import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.model.InsightValueFormat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alexander Seeliger on 24.10.2019.
 */
public class ActivityMetric extends ClusterMetric {

    public ActivityMetric(String logName) {
        super(logName);
    }

    @Override
    protected Insight generateInsight(double effectSize, List<? extends Measure> labels, double[] with, double[] without) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);

        insight.setLabels(labels.stream().map(Measure::getAttributeValue).collect(Collectors.toList()));
        insight.setWithin(with);
        insight.setWithout(without);

        insight.setFormat(InsightValueFormat.DISTRIBUTION);
        insight.setTitle("Activities");

        insight.setAnalysisTargetCodes(Arrays.asList(AnalysisTargetCodes.OUTLIERS, AnalysisTargetCodes.EXTREMES));
        insight.setDomainCodes(Arrays.asList(DomainCodes.CASE_PERSPECTIVE, DomainCodes.PROCESS_DISCOVERY));
        insight.setVisualizationCodes(Arrays.asList(VisualizationCodes.TABLE, VisualizationCodes.BAR_CHART));
        insight.setEventAttributeCodes(Arrays.asList(EventAttributeCodes.ACTIVITY));
        return insight;
    }

    @Override
    protected Map<ClusterMetric.Measure, Double> computeDifference(Object expression, Condition conditions) {
        var sql = new SelectQuery()
                .addColumns(db.eventEventNameCol)
                .addAliasedColumn(expression, "expr")
                .addCondition(conditions)
                .addJoins(SelectQuery.JoinType.INNER, db.eventCaseJoin, db.caseCaseAttributeJoin, db.caseVariantJoin)
                .addGroupings(db.eventEventNameCol);

        var result = jdbcTemplate.queryForList(sql.validate().toString());

        var measures = new HashMap<ClusterMetric.Measure, Double>();

        for (var item : result) {
            var measure = new ClusterMetric.Measure("event_name", item.get(db.eventEventNameCol.getColumnNameSQL()).toString());
            measures.put(measure, Double.parseDouble(item.get("expr").toString()));
        }

        return measures;
    }

    @Override
    protected Object getExpression() {
        return FunctionCall.countAll();
    }

}
