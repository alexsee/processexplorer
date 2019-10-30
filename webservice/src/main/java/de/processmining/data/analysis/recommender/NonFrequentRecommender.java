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
import de.processmining.data.model.FieldValue;
import de.processmining.data.analysis.itemsets.FrequentItemsetMiner;
import de.processmining.data.model.Insight;
import de.processmining.data.model.Recommendation;
import de.processmining.data.query.CasesQuery;
import de.processmining.data.query.QueryService;
import de.processmining.data.query.condition.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smile.association.ItemSet;

import java.util.*;

/**
 * @author Alexander Seeliger on 22.10.2019.
 */
@Service
public class NonFrequentRecommender {

    private final double MIN_SUPPORT = 0.9;

    private QueryService queryService;
    private DifferenceAnalysis differenceAnalysis;
    private FrequentItemsetMiner frequentItemsetMiner;

    @Autowired
    public NonFrequentRecommender(QueryService queryService,
                                  DifferenceAnalysis differenceAnalysis,
                                  FrequentItemsetMiner frequentItemsetMiner) {
        this.queryService = queryService;
        this.differenceAnalysis = differenceAnalysis;
        this.frequentItemsetMiner = frequentItemsetMiner;
    }

    public List<Recommendation> getRecommendations(String logName) {
        var result = new ArrayList<Recommendation>();

        // first obtain frequent closed itemsets
        var itemsetValues = new HashMap<FieldValue, Integer>();
        var itemsets = getFrequentItemSets(logName, itemsetValues);

        var fields = frequentItemsetMiner.getReversed(itemsetValues);

        // now we search for non frequent behavior
        itemsets.parallelStream().forEach(itemset -> {
            var conditions = new ArrayList<Condition>();

            for (var fIndex : itemset.items) {
                var field = fields.get(fIndex);

                if (field.getValue() == null) {
                    conditions.add(new AttributeCondition(field.getName(), AttributeCondition.BinaryType.EQUAL_TO, null));
                } else {
                    conditions.add(new AttributeCondition(field.getName(), AttributeCondition.BinaryType.EQUAL_TO, new Object[]{field.getValue()}));
                }
            }

            var c = new ArrayList<Condition>();
            c.add(new NotCondition(new ComboCondition(ComboType.AND, conditions)));

            // compute insights
            List<Insight> insights = differenceAnalysis.getInsights(differenceAnalysis.getDefaultMetrics(logName), c);

            if (insights.size() > 0) {
                var recommendation = new Recommendation();
                recommendation.setConditions(conditions);
                recommendation.setScore(insights.stream().mapToDouble(x -> Math.abs(x.getEffectSize())).sum() / insights.size());

                result.add(recommendation);
            }
        });

        return result;
    }

    private List<ItemSet> getFrequentItemSets(String logName, Map<FieldValue, Integer> itemsetValues) {
        var categoricalAttributes = queryService.getCategoricalCaseAttributes(logName);
        var cases = queryService.getCases(new CasesQuery(logName, new ArrayList<>(), categoricalAttributes));

//        return frequentItemsetMiner.getClosedItemsets(cases, itemsetValues, MIN_SUPPORT);
        return null;
    }

}
