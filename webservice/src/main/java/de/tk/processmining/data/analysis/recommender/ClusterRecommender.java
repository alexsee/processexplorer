package de.tk.processmining.data.analysis.recommender;

import de.tk.processmining.data.analysis.DifferenceAnalysis;
import de.tk.processmining.data.analysis.itemsets.FrequentItemsetMiner;
import de.tk.processmining.data.model.Recommendation;
import de.tk.processmining.data.query.QueryService;
import de.tk.processmining.data.query.condition.ClusterCondition;
import de.tk.processmining.data.query.condition.Condition;
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

            result.add(recommendation);
        }

        return result;
    }
}
