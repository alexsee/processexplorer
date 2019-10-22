package de.tk.processmining.data.analysis.recommender;

import de.tk.processmining.data.analysis.DifferenceAnalysis;
import de.tk.processmining.data.analysis.clustering.FieldValue;
import de.tk.processmining.data.analysis.itemsets.FrequentItemsetMiner;
import de.tk.processmining.data.model.Insight;
import de.tk.processmining.data.model.Recommendation;
import de.tk.processmining.data.query.CasesQuery;
import de.tk.processmining.data.query.QueryService;
import de.tk.processmining.data.query.condition.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import smile.association.ItemSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        return frequentItemsetMiner.getClosedItemsets(cases, itemsetValues, MIN_SUPPORT);
    }

}
