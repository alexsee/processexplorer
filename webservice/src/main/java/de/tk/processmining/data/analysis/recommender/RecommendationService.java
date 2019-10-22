package de.tk.processmining.data.analysis.recommender;

import de.tk.processmining.data.model.Recommendation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Seeliger on 22.10.2019.
 */
@Service
public class RecommendationService {

    private NonFrequentRecommender nonFrequentRecommender;
    private ClusterRecommender clusterRecommender;

    @Autowired
    public RecommendationService(NonFrequentRecommender nonFrequentRecommender, ClusterRecommender clusterRecommender) {
        this.nonFrequentRecommender = nonFrequentRecommender;
        this.clusterRecommender = clusterRecommender;
    }

    public List<Recommendation> getRecommendations(String logName) {
        var result = new ArrayList<Recommendation>();
        //        result.addAll(nonFrequentRecommender.getRecommendations(logName));
        result.addAll(clusterRecommender.getRecommendations(logName));
        return result;
    }

}
