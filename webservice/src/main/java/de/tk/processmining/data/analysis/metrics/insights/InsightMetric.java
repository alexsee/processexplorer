package de.tk.processmining.data.analysis.metrics.insights;

import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.query.condition.Condition;

import java.util.List;

public interface InsightMetric {

    List<Insight> getInsights(List<Condition> condition);

}
