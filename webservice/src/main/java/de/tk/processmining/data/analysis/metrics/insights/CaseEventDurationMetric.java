package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.custom.postgresql.PgExtractDatePart;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.analysis.categorization.VisualizationCodes;
import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.model.InsightValueFormat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static de.tk.processmining.data.analysis.categorization.AnalysisTargetCodes.EXTREMES;
import static de.tk.processmining.data.analysis.categorization.AnalysisTargetCodes.OUTLIERS;
import static de.tk.processmining.data.analysis.categorization.DomainCodes.TIME_PERSPECTIVE;

/**
 * @author Alexander Seeliger on 01.10.2019.
 */
public class CaseEventDurationMetric extends CaseMetric<CaseMetric.Measure, String> {

    private DatabaseModel db;

    public CaseEventDurationMetric(String logName) {
        super(logName);
        this.db = new DatabaseModel(logName);
    }

    @Override
    protected Insight generateInsight(double effectSize, CaseMetric.Measure measure1, CaseMetric.Measure measure2, String activityName) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);
        insight.setAverageWithin(measure1.getAverage());
        insight.setAverageWithout(measure2.getAverage());
        insight.setStddevWithin(measure1.getStddev());
        insight.setStddevWithout(measure2.getStddev());
        insight.setFormat(InsightValueFormat.DURATION);
        insight.setTitle("Activity Duration");
        insight.setSubTitle(activityName);

        // set codes
        insight.setAnalysisTargetCodes(Arrays.asList(OUTLIERS, EXTREMES));
        insight.setDomainCodes(Arrays.asList(TIME_PERSPECTIVE));
        insight.setVisualizationCodes(Arrays.asList(VisualizationCodes.TABLE));
        return insight;
    }

    @Override
    protected Map<String, CaseMetric.Measure> computeDifference(Object calculation, Condition conditions) {
        var inner_sql = new SelectQuery()
                .addColumns(db.caseAttributeCaseIdCol)
                .addAliasedColumn(db.graphSourceEventCol, "event_name")
                .addAliasedColumn(calculation, "expr")
                .addCondition(conditions)
                .addJoins(SelectQuery.JoinType.INNER, db.graphCaseAttributeJoin, db.graphVariantJoin)
                .addGroupings(db.caseAttributeCaseIdCol, db.graphSourceEventCol);

        var outer_sql = new SelectQuery()
                .addAliasedColumn(new CustomExpression("a.event_name"), "event_name")
                .addAliasedColumn(FunctionCall.avg().addCustomParams(new CustomSql("a.expr")), "average")
                .addAliasedColumn(new CustomExpression("stddev(a.expr)"), "standard_deviation")
                .addCustomFromTable(AliasedObject.toAliasedObject(new CustomExpression(inner_sql.toString()), "a"))
                .addCustomGroupings(new CustomExpression("a.event_name"))
                .addHaving(new CustomCondition("stddev(a.expr) > 0"));

        var result = jdbcTemplate.queryForList(outer_sql.validate().toString());
        var measures = new HashMap<String, CaseMetric.Measure>();

        for (var item : result) {
            if (item.get("average") == null || item.get("standard_deviation") == null)
                continue;

            var measure = new CaseMetric.Measure(Double.parseDouble(item.get("average").toString()), Double.parseDouble(item.get("standard_deviation").toString()));
            measures.put(item.get("event_name").toString(), measure);
        }

        return measures;
    }

    @Override
    protected Object getExpression() {
        return FunctionCall.avg().addCustomParams(new ExtractExpression(PgExtractDatePart.EPOCH, db.graphDurationCol));
    }
}
