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

package de.processmining.data.analysis.recommender;

import de.processmining.data.analysis.DifferenceAnalysis;
import de.processmining.data.analysis.itemsets.FrequentItemsetMiner;
import de.processmining.data.model.Insight;
import de.processmining.data.model.Recommendation;
import de.processmining.data.query.QueryService;
import de.processmining.data.query.condition.ClusterCondition;
import de.processmining.data.query.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Alexander Seeliger on 22.10.2019.
 */
@Service
public class ClusterRecommender {

    private QueryService queryService;
    private DifferenceAnalysis differenceAnalysis;
    private FrequentItemsetMiner frequentItemsetMiner;

    @Autowired
    public ClusterRecommender(QueryService queryService,
                              DifferenceAnalysis differenceAnalysis,
                              FrequentItemsetMiner frequentItemsetMiner) {
        this.queryService = queryService;
        this.differenceAnalysis = differenceAnalysis;
        this.frequentItemsetMiner = frequentItemsetMiner;
    }

    public List<Recommendation> getRecommendations(String logName) {
        var result = new ArrayList<Recommendation>();
        var clusters = queryService.getClusterValues(logName);

        for (var cluster : clusters) {
            var recommendation = new Recommendation(1.0, Arrays.asList(new Condition[]{new ClusterCondition(cluster)}));
            var logStatistics = queryService.getLogStatistics(logName, recommendation.getConditions());
            recommendation.setNumTraces(logStatistics.getNumTraces());

            List<Insight> insights = differenceAnalysis.getInsights(differenceAnalysis.getDefaultMetrics(logName), recommendation.getConditions());
            recommendation.setScore(insights.stream().mapToDouble(x -> Math.abs(x.getEffectSize())).sum() / (insights.size() + 1));

            result.add(recommendation);
        }

        return result;
    }
}
