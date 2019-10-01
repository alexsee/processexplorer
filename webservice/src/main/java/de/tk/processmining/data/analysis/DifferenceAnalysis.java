package de.tk.processmining.data.analysis;

import de.tk.processmining.data.analysis.metrics.insights.*;
import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.query.QueryManager;
import de.tk.processmining.data.query.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.management.Query;
import java.util.ArrayList;
import java.util.List;

@Service
public class DifferenceAnalysis {

    private QueryManager queryManager;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DifferenceAnalysis(QueryManager queryManager, JdbcTemplate jdbcTemplate) {
        this.queryManager = queryManager;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Insight> getInsights(List<InsightMetric> metrics, List<Condition> conditions) {
        var result = new ArrayList<Insight>();

        for (var metric : metrics) {
            metric.setJdbcTemplate(jdbcTemplate);

            var insights = metric.getInsights(conditions);
            if (insights.size() > 0) {
                result.addAll(insights);
            }
        }

        return result;
    }

    public List<InsightMetric> getDefaultMetrics(String logName) {
        var result = new ArrayList<InsightMetric>();
        result.add(new EventOccurrenceMetric(logName));
        result.add(new EventDurationMetric(logName));
        result.add(new CaseDurationMetric(logName));

        queryManager.getCategoricalCaseAttributes(logName).forEach(attr -> result.add(new CaseAttributeMetric(logName, attr)));
        return result;
    }

}
