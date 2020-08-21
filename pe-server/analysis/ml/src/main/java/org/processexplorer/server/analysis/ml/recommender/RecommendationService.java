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

package org.processexplorer.server.analysis.ml.recommender;

import org.processexplorer.server.analysis.query.model.Recommendation;
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
    public RecommendationService(
            NonFrequentRecommender nonFrequentRecommender,
            ClusterRecommender clusterRecommender) {
        this.nonFrequentRecommender = nonFrequentRecommender;
        this.clusterRecommender = clusterRecommender;
    }

    public List<Recommendation> getRecommendations(String logName) {
        var result = new ArrayList<Recommendation>();
        // result.addAll(nonFrequentRecommender.getRecommendations(logName));
        result.addAll(clusterRecommender.getRecommendations(logName));
        return result;
    }

}
