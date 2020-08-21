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
import org.processexplorer.server.analysis.query.DatabaseModel;
import org.processexplorer.server.analysis.query.codes.VisualizationCodes;
import org.processexplorer.server.analysis.query.model.Insight;
import org.processexplorer.server.analysis.query.model.InsightValueFormat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.processexplorer.server.analysis.query.codes.AnalysisTargetCodes.EXTREMES;
import static org.processexplorer.server.analysis.query.codes.AnalysisTargetCodes.OUTLIERS;
import static org.processexplorer.server.analysis.query.codes.DomainCodes.TIME_PERSPECTIVE;

/**
 * @author Alexander Seeliger on 01.10.2019.
 */
public class CaseLengthMetric extends CaseMetric<CaseMetric.Measure, String> {

    private DatabaseModel db;

    public CaseLengthMetric(String logName) {
        super(logName);
        this.db = new DatabaseModel(logName);
    }

    @Override
    protected Insight generateInsight(double effectSize, CaseMetric.Measure measure1, CaseMetric.Measure measure2, String edge) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);
        insight.setAverageWithin(measure1.getAverage());
        insight.setAverageWithout(measure2.getAverage());
        insight.setStddevWithin(measure1.getStddev());
        insight.setStddevWithout(measure2.getStddev());
        insight.setFormat(InsightValueFormat.NUMBER);
        insight.setTitle("Case Length");

        // set codes
        insight.setAnalysisTargetCodes(Arrays.asList(OUTLIERS, EXTREMES));
        insight.setDomainCodes(Arrays.asList(TIME_PERSPECTIVE));
        insight.setVisualizationCodes(Arrays.asList(VisualizationCodes.TABLE));
        return insight;
    }

    @Override
    protected Map<String, CaseMetric.Measure> computeDifference(Object calculation, Condition conditions) {
        var inner_sql = new SelectQuery()
                .addColumns(db.caseCaseIdCol)
                .addAliasedColumn(calculation, "expr")
                .addCondition(conditions)
                .addJoins(SelectQuery.JoinType.INNER, db.caseCaseAttributeJoin, db.caseVariantJoin);

        var outer_sql = new SelectQuery()
                .addAliasedColumn(FunctionCall.avg().addCustomParams(new CustomSql("a.expr")), "average")
                .addAliasedColumn(new CustomExpression("stddev(a.expr)"), "standard_deviation")
                .addCustomFromTable(AliasedObject.toAliasedObject(new CustomExpression(inner_sql.toString()), "a"))
                .addHaving(new CustomCondition("stddev(a.expr) > 0"));

        var result = jdbcTemplate.queryForList(outer_sql.validate().toString());
        var measures = new HashMap<String, CaseMetric.Measure>();

        for (var item : result) {
            if (item.get("average") == null || item.get("standard_deviation") == null)
                continue;

            var measure = new CaseMetric.Measure(Double.parseDouble(item.get("average").toString()), Double.parseDouble(item.get("standard_deviation").toString()));
            measures.put("case_length", measure);
        }

        return measures;
    }

    @Override
    protected Object getExpression() {
        return db.caseNumEventsCol;
    }
}
