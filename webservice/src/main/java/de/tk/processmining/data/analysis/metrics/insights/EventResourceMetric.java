package de.tk.processmining.data.analysis.metrics.insights;

import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.model.InsightValueFormat;

import java.util.List;
import java.util.stream.Collectors;

public class EventResourceMetric extends EventMetric {

    public EventResourceMetric(String logName, String eventName) {
        super(logName, eventName);
    }

    @Override
    protected Insight generateInsight(double effectSize, List<? extends ClusterMetric.Measure> labels, double[] with, double[] without) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);

        insight.setLabels(labels.stream().map(ClusterMetric.Measure::getAttributeValue).collect(Collectors.toList()));
        insight.setWithin(with);
        insight.setWithout(without);

        insight.setFormat(InsightValueFormat.DISTRIBUTION);
        insight.setTitle("Resource");
        insight.setSubTitle(this.eventName);
        return insight;
    }

    @Override
    protected Object getExpression() {
        return db.eventResourceCol;
    }
}
