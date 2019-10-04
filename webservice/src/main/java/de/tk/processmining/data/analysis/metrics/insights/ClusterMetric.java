package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.NotCondition;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.analysis.metrics.StatisticMetrics;
import de.tk.processmining.data.model.Insight;
import org.springframework.jdbc.core.JdbcTemplate;
import smile.math.Math;

import java.util.*;

import static de.tk.processmining.data.analysis.metrics.StatisticMetrics.norm;

/**
 * @author Alexander Seeliger on 01.10.2019.
 */
public abstract class ClusterMetric implements InsightMetric {

    protected DatabaseModel db;

    protected JdbcTemplate jdbcTemplate;

    protected String logName;

    protected ClusterMetric(String logName) {
        this.logName = logName;
        this.db = new DatabaseModel(logName);
    }

    @Override
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Insight> getInsights(List<de.tk.processmining.data.query.condition.Condition> queryConditions) {
        var calculation = getExpression();

        var conditions = new ArrayList<>();
        for (var rule : queryConditions) {
            conditions.addAll(rule.getCondition(db));
        }

        // get occurrence
        var withCondition = computeDifference(calculation, ComboCondition.and(conditions.toArray()));
        var withoutCondition = computeDifference(calculation, new NotCondition(ComboCondition.and(conditions.toArray())));

        var combinations = new HashSet<Measure>();
        combinations.addAll(withCondition.keySet());
        combinations.addAll(withoutCondition.keySet());

        var with = new double[combinations.size()];
        var without = new double[combinations.size()];

        int i = 0;
        for (var attr : combinations) {
            with[i] = withCondition.getOrDefault(attr, 0D);
            without[i] = withoutCondition.getOrDefault(attr, 0D);

            i++;
        }

        var result = new ArrayList<Insight>();
        var effectSize = StatisticMetrics.effectByCohensD(with, without);
        var divergence = Math.JensenShannonDivergence(norm(with), norm(without));

        if (Math.sqrt(divergence) < 0.6 && combinations.size() > 1) {
            result.add(generateInsight(effectSize, new ArrayList<>(combinations), with, without));
        }

        return result;
    }

    protected abstract Insight generateInsight(double effectSize, List<? extends Measure> labels, double[] with, double[] without);

    protected abstract Map<? extends Measure, Double> computeDifference(Object calculation, Condition conditions);

    protected abstract Object getExpression();

    public class Measure {
        private String attributeName;

        private String attributeValue;

        public Measure(String attributeName, String attributeValue) {
            this.attributeName = attributeName;
            this.attributeValue = attributeValue;
        }

        public String getAttributeName() {
            return attributeName;
        }

        public void setAttributeName(String attributeName) {
            this.attributeName = attributeName;
        }

        public String getAttributeValue() {
            return attributeValue;
        }

        public void setAttributeValue(String attributeValue) {
            this.attributeValue = attributeValue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(attributeName, attributeValue);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Measure) {
                var other = (Measure) obj;
                return other.getAttributeName().equals(getAttributeName()) && other.getAttributeValue().equals(getAttributeValue());
            }
            return false;
        }
    }
}
