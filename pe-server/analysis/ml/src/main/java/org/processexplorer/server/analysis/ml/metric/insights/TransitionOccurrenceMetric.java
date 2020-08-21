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

import com.healthmarketscience.sqlbuilder.FunctionCall;
import org.processexplorer.server.analysis.query.codes.AnalysisTargetCodes;
import org.processexplorer.server.analysis.query.codes.DomainCodes;
import org.processexplorer.server.analysis.query.codes.VisualizationCodes;
import org.processexplorer.server.analysis.query.model.Insight;
import org.processexplorer.server.analysis.query.model.InsightValueFormat;

import java.util.Arrays;

public class TransitionOccurrenceMetric extends TransitionMetric {

    public TransitionOccurrenceMetric(String logName) {
        super(logName);
    }

    @Override
    protected Object getExpression() {
        return FunctionCall.countAll();
    }

    protected Insight generateInsight(double effectSize, CaseMetric.Measure measure1, CaseMetric.Measure measure2, Edge edge) {
        var insight = new Insight();
        insight.setEffectSize(effectSize);
        insight.setCasesWithin(measure1.getNumberOfCases());
        insight.setCasesWithout(measure2.getNumberOfCases());
        insight.setAverageWithin(measure1.getAverage());
        insight.setAverageWithout(measure2.getAverage());
        insight.setStddevWithin(measure1.getStddev());
        insight.setStddevWithout(measure2.getStddev());
        insight.setFormat(InsightValueFormat.NUMBER);

        if (edge.getSourceEvent().equals(edge.getTargetEvent())) {
            insight.setTitle("Loop");
            insight.setSubTitle(edge.getSourceEvent());
        } else {
            insight.setTitle("Occurrence");
            insight.setSubTitle(edge.getSourceEvent() + " --> " + edge.getTargetEvent());
        }

        insight.setAnalysisTargetCodes(Arrays.asList(AnalysisTargetCodes.OUTLIERS, AnalysisTargetCodes.EXTREMES));
        insight.setDomainCodes(Arrays.asList(DomainCodes.PROCESS_DISCOVERY));
        insight.setVisualizationCodes(Arrays.asList(VisualizationCodes.TABLE));
        return insight;
    }

}
