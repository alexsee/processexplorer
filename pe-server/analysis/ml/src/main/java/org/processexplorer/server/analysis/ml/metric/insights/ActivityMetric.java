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

import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import org.processexplorer.server.analysis.query.codes.AnalysisTargetCodes;
import org.processexplorer.server.analysis.query.codes.DomainCodes;
import org.processexplorer.server.analysis.query.codes.EventAttributeCodes;
import org.processexplorer.server.analysis.query.codes.VisualizationCodes;
import org.processexplorer.server.analysis.query.model.Insight;
import org.processexplorer.server.analysis.query.model.InsightValueFormat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alexander Seeliger on 24.10.2019.
 */
public class ActivityMetric extends ClusterMetric {

    public ActivityMetric(String logName) {
        super(logName);
    }

    @Override
    protected Insight generateInsight(double effectSize, List<? extends Measure> labels, double[] with, double[] without) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);

        insight.setLabels(labels.stream().map(Measure::getAttributeValue).collect(Collectors.toList()));
        insight.setWithin(with);
        insight.setWithout(without);

        insight.setFormat(InsightValueFormat.DISTRIBUTION);
        insight.setTitle("Activities");

        insight.setAnalysisTargetCodes(Arrays.asList(AnalysisTargetCodes.OUTLIERS, AnalysisTargetCodes.EXTREMES));
        insight.setDomainCodes(Arrays.asList(DomainCodes.CASE_PERSPECTIVE, DomainCodes.PROCESS_DISCOVERY));
        insight.setVisualizationCodes(Arrays.asList(VisualizationCodes.TABLE, VisualizationCodes.BAR_CHART));
        insight.setEventAttributeCodes(Arrays.asList(EventAttributeCodes.ACTIVITY));
        return insight;
    }

    @Override
    protected Map<ClusterMetric.Measure, Double> computeDifference(Object expression, Condition conditions) {
        var sql = new SelectQuery()
                .addColumns(db.eventEventCol)
                .addAliasedColumn(expression, "expr")
                .addCondition(conditions)
                .addJoins(SelectQuery.JoinType.INNER, db.eventCaseJoin, db.caseCaseAttributeJoin)
                .addGroupings(db.eventEventCol);

        var result = jdbcTemplate.queryForList(sql.validate().toString());

        var measures = new HashMap<ClusterMetric.Measure, Double>();

        for (var item : result) {
            var measure = new ClusterMetric.Measure("event_name", item.get(db.eventEventCol.getColumnNameSQL()).toString());
            measures.put(measure, Double.parseDouble(item.get("expr").toString()));
        }

        return measures;
    }

    @Override
    protected Object getExpression() {
        return FunctionCall.countAll();
    }

}
