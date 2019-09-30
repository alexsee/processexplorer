package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.ExtractExpression;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import com.healthmarketscience.sqlbuilder.custom.postgresql.PgExtractDatePart;
import de.tk.processmining.data.model.Insight;
import org.springframework.jdbc.core.JdbcTemplate;

public class OccurrenceMetric extends GraphCaseMetric {

    public OccurrenceMetric(JdbcTemplate jdbcTemplate, String logName) {
        super(jdbcTemplate, logName);
    }

    @Override
    protected Object getExpression() {
        return FunctionCall.avg().addCustomParams(new ExtractExpression(PgExtractDatePart.EPOCH, new CustomSql("duration")));
    }

    protected Insight generateInsight(double effectSize, CaseMetric.Measure measure1, CaseMetric.Measure measure2, Edge edge) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);
        insight.setInsight("Occurrence of \"" + edge.getSourceEvent() + " --> " + edge.getTargetEvent() + "\" with " + measure1.getAverage() + " vs " + measure2.getAverage());
        return insight;
    }

}
