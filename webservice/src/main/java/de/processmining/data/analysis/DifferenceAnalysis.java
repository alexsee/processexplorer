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

package de.processmining.data.analysis;

import de.processmining.data.analysis.metrics.insights.*;
import de.processmining.data.model.Insight;
import de.processmining.data.query.QueryService;
import de.processmining.data.query.condition.Condition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DifferenceAnalysis {

    private Logger logger = LoggerFactory.getLogger(DifferenceAnalysis.class);

    private QueryService queryService;
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public DifferenceAnalysis(QueryService queryService, JdbcTemplate jdbcTemplate) {
        this.queryService = queryService;
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Insight> getInsights(List<InsightMetric> metrics, List<Condition> conditions) {
        var result = new ArrayList<Insight>();

        metrics.parallelStream().forEach(metric -> {
            logger.debug("Compute {} metric", metric.getClass().getCanonicalName());
            metric.setJdbcTemplate(jdbcTemplate);

            var insights = metric.getInsights(conditions);
            if (insights.size() > 0) {
                result.addAll(insights);
            }

            logger.debug("Finished {} metric with {} insights", metric.getClass().getCanonicalName(), insights.size());
        });

        return result;
    }

    public List<InsightMetric> getDefaultMetrics(String logName) {
        var result = new ArrayList<InsightMetric>();
        result.add(new TransitionOccurrenceMetric(logName));
        result.add(new TransitionDurationMetric(logName));
        result.add(new CaseDurationMetric(logName));
        result.add(new CaseEventDurationMetric(logName));
        result.add(new ActivityMetric(logName));

        queryService.getCategoricalCaseAttributes(logName).forEach(attr -> result.add(new CaseAttributeMetric(logName, attr)));
        queryService.getLogStatistics(logName).getActivities().forEach(evt -> result.add(new EventResourceMetric(logName, evt)));
        return result;
    }

}
