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

    @Autowired
    public RecommendationService(NonFrequentRecommender nonFrequentRecommender) {
        this.nonFrequentRecommender = nonFrequentRecommender;
    }

    public List<Recommendation> getRecommendations(String logName) {
        var result = new ArrayList<Recommendation>();
        result.addAll(nonFrequentRecommender.getRecommendations(logName));

        return result;
    }

}
