package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.custom.postgresql.PgExtractDatePart;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.model.Insight;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Seeliger on 30.09.2019.
 */
public abstract class CaseMetric<X extends CaseMetric.Measure, Y> implements InsightMetric {

    protected final JdbcTemplate jdbcTemplate;
    protected final String logName;

    protected CaseMetric(JdbcTemplate jdbcTemplate, String logName) {
        this.jdbcTemplate = jdbcTemplate;
        this.logName = logName;
    }

    protected abstract Insight generateInsight(double effectSize, X measure1, X measure2, Y edge);

    protected abstract Map<Y, X> computeDifference(Object calculation, Condition conditions);

    protected abstract Object getExpression();

    public List<Insight> getInsights(List<de.tk.processmining.data.query.condition.Condition> queryConditions) {
        var db = new DatabaseModel(this.logName);
        var calculation = getExpression();

        var conditions = new ArrayList<>();
        for (var rule : queryConditions) {
            conditions.addAll(rule.getCondition(db));
        }

        // get occurrence
        var withCondition = computeDifference(calculation,  ComboCondition.and(conditions.toArray()));
        var withoutCondition = computeDifference(calculation, new NotCondition(ComboCondition.and(conditions.toArray())));

        var result = new ArrayList<Insight>();

        for (var item : withCondition.entrySet()) {
            var other = withoutCondition.get(item.getKey());

            var effectSize = (item.getValue().getAverage() - other.getAverage()) / Math.sqrt((Math.pow(item.getValue().getStddev(), 2) + Math.pow(other.getStddev(), 2)) / 2);
            if (Math.abs(effectSize) > 0.2) {
                result.add(generateInsight(effectSize, item.getValue(), other, item.getKey()));
            }
        }

        return result;
    }


    protected class Measure {
        private double average;
        private double stddev;

        public Measure(double average, double stddev) {
            this.average = average;
            this.stddev = stddev;
        }

        public double getAverage() {
            return average;
        }

        public void setAverage(double average) {
            this.average = average;
        }

        public double getStddev() {
            return stddev;
        }

        public void setStddev(double stddev) {
            this.stddev = stddev;
        }
    }

}
