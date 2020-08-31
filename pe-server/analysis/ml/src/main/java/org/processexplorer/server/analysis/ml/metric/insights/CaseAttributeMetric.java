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

import com.healthmarketscience.sqlbuilder.*;
import org.processexplorer.server.analysis.query.codes.AnalysisTargetCodes;
import org.processexplorer.server.analysis.query.codes.DomainCodes;
import org.processexplorer.server.analysis.query.codes.EventAttributeCodes;
import org.processexplorer.server.analysis.query.codes.VisualizationCodes;
import org.processexplorer.server.analysis.query.model.Insight;
import org.processexplorer.server.analysis.query.model.InsightValueFormat;
import org.processexplorer.server.common.persistence.repository.EventLogAnnotationRepository;
import org.processexplorer.server.common.persistence.entity.EventLogAnnotation;
import org.processexplorer.server.common.utils.ApplicationContextProvider;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alexander Seeliger on 01.10.2019.
 */
public class CaseAttributeMetric extends ClusterMetric {

    private final String attributeName;

    public CaseAttributeMetric(String logName, String attributeName) {
        super(logName);
        this.attributeName = attributeName;
    }

    @Override
    protected Insight generateInsight(double effectSize, List<? extends Measure> labels, double[] with, double[] without) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);

        insight.setLabels(labels.stream().map(Measure::getAttributeValue).collect(Collectors.toList()));
        insight.setWithin(with);
        insight.setWithout(without);

        insight.setFormat(InsightValueFormat.DISTRIBUTION);
        insight.setTitle("Case Attribute");
        insight.setSubTitle(attributeName);

        insight.setAnalysisTargetCodes(Arrays.asList(AnalysisTargetCodes.OUTLIERS, AnalysisTargetCodes.EXTREMES));
        insight.setDomainCodes(Arrays.asList(DomainCodes.CASE_PERSPECTIVE));
        insight.setVisualizationCodes(Arrays.asList(VisualizationCodes.TABLE, VisualizationCodes.BAR_CHART));
        insight.setEventAttributeCodes(getEventAttributeCodes());
        return insight;
    }

    private List<EventAttributeCodes> getEventAttributeCodes() {
        var context = ApplicationContextProvider.getApplicationContext();
        var eventLogAnnotationRepository = context.getBean(EventLogAnnotationRepository.class);

        var annotations = eventLogAnnotationRepository.findByLogNameAndColumnTypeAndColumnName(logName, "case_attribute", attributeName);
        return annotations.stream().map(EventLogAnnotation::getCode).map(EventAttributeCodes::valueOf).collect(Collectors.toList());
    }

    @Override
    protected Map<ClusterMetric.Measure, Double> computeDifference(Object calculation, Condition conditions) {
        var inner_sql = new SelectQuery()
                .addAliasedColumn(FunctionCall.countAll(), "occurrence")
                .addAliasedColumn(calculation, "attr")
                .addFromTable(db.caseTable)
                .addJoins(SelectQuery.JoinType.INNER, db.caseCaseAttributeJoin)
                .addCondition(conditions)
                .addCustomGroupings(calculation);

        var result = jdbcTemplate.queryForList(inner_sql.validate().toString());
        var measures = new HashMap<ClusterMetric.Measure, Double>();

        for (var item : result) {
            var value = item.get("attr");

            var measure = new ClusterMetric.Measure(attributeName, value == null ? "" : value.toString());
            measures.put(measure, Double.parseDouble(item.get("occurrence").toString()));
        }

        return measures;
    }

    @Override
    protected Object getExpression() {
        return db.caseAttributeTable.addColumn("\"" + attributeName + "\"");
    }

}
