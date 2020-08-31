/*
 * ProcessExplorer
 * Copyright (C) 2019  Alexander Seeliger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.processexplorer.server.analysis.ml.metric.insights;

import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.NotCondition;
import org.processexplorer.server.analysis.ml.metric.StatisticMetrics;
import org.processexplorer.server.analysis.query.DatabaseModel;
import org.processexplorer.server.analysis.query.model.Insight;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;

import static org.processexplorer.server.analysis.ml.metric.StatisticMetrics.norm;
import static org.apache.commons.math3.stat.StatUtils.sum;
import static smile.math.MathEx.JensenShannonDivergence;

/**
 * @author Alexander Seeliger on 01.10.2019.
 */
public abstract class ClusterMetric implements InsightMetric {

    public double minSamples = 10;

    public double maxDivergence = 0.6;

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
    public List<Insight> getInsights(List<org.processexplorer.server.analysis.query.condition.Condition> queryConditions) {
        var calculation = getExpression();

        var conditions = new ArrayList<>();
        for (var rule : queryConditions) {
            conditions.add(rule.getCondition(db));
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
        var divergence = JensenShannonDivergence(norm(with), norm(without));

        if (Math.sqrt(divergence) >= maxDivergence && combinations.size() > 1 && sum(with) > minSamples) {
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
