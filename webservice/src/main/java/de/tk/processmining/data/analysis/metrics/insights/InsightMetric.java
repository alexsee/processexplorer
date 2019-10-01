package de.tk.processmining.data.analysis.metrics.insights;

import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.query.condition.Condition;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public interface InsightMetric {

    void setJdbcTemplate(JdbcTemplate jdbcTemplate);

    List<Insight> getInsights(List<Condition> conditions);

}
