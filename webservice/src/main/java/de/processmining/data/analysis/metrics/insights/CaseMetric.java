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

package de.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.ComboCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.NotCondition;
import de.processmining.data.DatabaseModel;
import de.processmining.data.model.Insight;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Alexander Seeliger on 30.09.2019.
 */
public abstract class CaseMetric<X extends CaseMetric.Measure, Y> implements InsightMetric {

    protected DatabaseModel db;
    protected JdbcTemplate jdbcTemplate;
    protected final String logName;

    protected CaseMetric(String logName) {
        this.logName = logName;
        this.db = new DatabaseModel(logName);
    }

    @Override
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    protected abstract Insight generateInsight(double effectSize, X measure1, X measure2, Y edge);

    protected abstract Map<Y, X> computeDifference(Object calculation, Condition conditions);

    protected abstract Object getExpression();

    public List<Insight> getInsights(List<de.processmining.data.query.condition.Condition> queryConditions) {
        var calculation = getExpression();

        var conditions = new ArrayList<>();
        for (var rule : queryConditions) {
            conditions.add(rule.getCondition(db));
        }

        // get occurrence
        var withCondition = computeDifference(calculation, ComboCondition.and(conditions.toArray()));
        var withoutCondition = computeDifference(calculation, new NotCondition(ComboCondition.and(conditions.toArray())));

        var result = new ArrayList<Insight>();

        for (var item : withCondition.entrySet()) {
            var other = withoutCondition.get(item.getKey());

            if (other == null) {
                continue;
            }

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
