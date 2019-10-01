package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.custom.postgresql.PgExtractDatePart;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.model.InsightValueFormat;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexander Seeliger on 01.10.2019.
 */
public class CaseDurationMetric extends CaseMetric<CaseMetric.Measure, String> {

    private DatabaseModel db;

    public CaseDurationMetric(String logName) {
        super(logName);
        this.db = new DatabaseModel(logName);
    }

    @Override
    protected Insight generateInsight(double effectSize, CaseMetric.Measure measure1, CaseMetric.Measure measure2, String edge) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);
        insight.setAverageWithin(measure1.getAverage());
        insight.setAverageWithout(measure2.getAverage());
        insight.setStddevWithin(measure1.getStddev());
        insight.setStddevWithout(measure2.getStddev());
        insight.setFormat(InsightValueFormat.DURATION);
        insight.setInsight("Case Duration");
        return insight;
    }

    @Override
    protected Map<String, CaseMetric.Measure> computeDifference(Object calculation, Condition conditions) {
        var inner_sql = new SelectQuery()
                .addColumns(db.caseCaseIdCol)
                .addAliasedColumn(calculation, "expr")
                .addCondition(conditions)
                .addJoins(SelectQuery.JoinType.INNER, db.caseCaseAttributeJoin, db.caseVariantJoin);

        var outer_sql = new SelectQuery()
                .addAliasedColumn(FunctionCall.avg().addCustomParams(new CustomSql("a.expr")), "average")
                .addAliasedColumn(new CustomExpression("stddev(a.expr)"), "standard_deviation")
                .addCustomFromTable(AliasedObject.toAliasedObject(new CustomExpression(inner_sql.toString()), "a"))
                .addHaving(new CustomCondition("stddev(a.expr) > 0"));

        var result = jdbcTemplate.queryForList(outer_sql.validate().toString());
        var measures = new HashMap<String, CaseMetric.Measure>();

        for (var item : result) {
            var measure = new CaseMetric.Measure(Double.parseDouble(item.get("average").toString()), Double.parseDouble(item.get("standard_deviation").toString()));
            measures.put("case_duration", measure);
        }

        return measures;
    }

    @Override
    protected Object getExpression() {
        return new ExtractExpression(PgExtractDatePart.EPOCH, db.caseDurationCol);
    }
}
