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

import org.processexplorer.server.analysis.query.codes.AnalysisTargetCodes;
import org.processexplorer.server.analysis.query.codes.DomainCodes;
import org.processexplorer.server.analysis.query.codes.VisualizationCodes;
import org.processexplorer.server.analysis.query.model.Insight;
import org.processexplorer.server.analysis.query.model.InsightValueFormat;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EventResourceMetric extends EventMetric {

    public EventResourceMetric(String logName, String eventName) {
        super(logName, eventName);
    }

    @Override
    protected Insight generateInsight(double effectSize, List<? extends ClusterMetric.Measure> labels, double[] with, double[] without) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);

        insight.setLabels(labels.stream().map(ClusterMetric.Measure::getAttributeValue).collect(Collectors.toList()));
        insight.setWithin(with);
        insight.setWithout(without);

        insight.setFormat(InsightValueFormat.DISTRIBUTION);
        insight.setTitle("Resource");
        insight.setSubTitle(this.eventName);

        insight.setAnalysisTargetCodes(Arrays.asList(AnalysisTargetCodes.DISTRIBUTION));
        insight.setDomainCodes(Arrays.asList(DomainCodes.ORGANIZATIONAL_PERSPECTIVE));
        insight.setVisualizationCodes(Arrays.asList(VisualizationCodes.BAR_CHART));
        return insight;
    }

    @Override
    protected Object getExpression() {
        return db.eventEventCol;
    }
}
