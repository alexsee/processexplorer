package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.FunctionCall;
import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.model.InsightValueFormat;

public class EventOccurrenceMetric extends EventMetric {

    public EventOccurrenceMetric(String logName) {
        super(logName);
    }

    @Override
    protected Object getExpression() {
        return FunctionCall.countAll();
    }

    protected Insight generateInsight(double effectSize, CaseMetric.Measure measure1, CaseMetric.Measure measure2, Edge edge) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);
        insight.setAverageWithin(measure1.getAverage());
        insight.setAverageWithout(measure2.getAverage());
        insight.setStddevWithin(measure1.getStddev());
        insight.setStddevWithout(measure2.getStddev());
        insight.setFormat(InsightValueFormat.NUMBER);
        insight.setInsight("Occurrence of \"" + edge.getSourceEvent() + " --> " + edge.getTargetEvent() + "\"");
        return insight;
    }

}
