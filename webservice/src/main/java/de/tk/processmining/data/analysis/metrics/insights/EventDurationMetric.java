package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.ExtractExpression;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import com.healthmarketscience.sqlbuilder.custom.postgresql.PgExtractDatePart;
import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.model.InsightValueFormat;

/**
 * @author Alexander Seeliger on 01.10.2019.
 */
public class EventDurationMetric extends EventMetric {

    public EventDurationMetric(String logName) {
        super(logName);
    }

    @Override
    protected Object getExpression() {
        return FunctionCall.avg().addCustomParams(new ExtractExpression(PgExtractDatePart.EPOCH, new CustomSql("duration")));
    }

    protected Insight generateInsight(double effectSize, CaseMetric.Measure measure1, CaseMetric.Measure measure2, Edge edge) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);
        insight.setAverageWithin(measure1.getAverage());
        insight.setAverageWithout(measure2.getAverage());
        insight.setStddevWithin(measure1.getStddev());
        insight.setStddevWithout(measure2.getStddev());
        insight.setFormat(InsightValueFormat.DURATION);
        insight.setInsight("Duration between \"" + edge.getSourceEvent() + " --> " + edge.getTargetEvent() + "\"");
        return insight;
    }

}
