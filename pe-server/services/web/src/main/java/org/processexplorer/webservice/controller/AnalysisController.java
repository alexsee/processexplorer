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

package org.processexplorer.webservice.controller;

import org.processexplorer.server.analysis.ml.clustering.MultiPerspectiveTraceClustering;
import org.processexplorer.server.analysis.ml.clustering.SimpleTraceClustering;
import org.processexplorer.server.analysis.ml.recommender.DifferenceAnalysis;
import org.processexplorer.server.analysis.query.model.Insight;
import org.processexplorer.server.analysis.query.model.Recommendation;
import org.processexplorer.server.analysis.query.condition.Condition;
import org.processexplorer.webservice.services.LogRecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private SimpleTraceClustering traceClustering;

    private MultiPerspectiveTraceClustering multiPerspectiveTraceClustering;

    private DifferenceAnalysis differenceAnalysis;

    private LogRecommendationService logRecommendationService;

    @Autowired
    public AnalysisController(SimpleTraceClustering traceClustering, MultiPerspectiveTraceClustering multiPerspectiveTraceClustering, DifferenceAnalysis differenceAnalysis, LogRecommendationService logRecommendationService) {
        this.traceClustering = traceClustering;
        this.multiPerspectiveTraceClustering = multiPerspectiveTraceClustering;
        this.differenceAnalysis = differenceAnalysis;
        this.logRecommendationService = logRecommendationService;
    }

    @PostMapping("/simple_trace_clustering")
    public ResponseEntity simpleTraceClustering(@RequestParam("logName") String logName) {
        traceClustering.cluster(logName);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/insights")
    public ResponseEntity<List<Insight>> insights(@RequestParam("logName") String logName, @RequestBody List<Condition> conditions) {
        var diffAnalysis = differenceAnalysis.getInsights(differenceAnalysis.getDefaultMetrics(logName), conditions);
        return ResponseEntity.ok(diffAnalysis);
    }

    @PostMapping("/multi_trace_clustering")
    public ResponseEntity multiTraceClustering(@RequestParam("logName") String logName) {
        multiPerspectiveTraceClustering.cluster(logName);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/recommendations")
    public ResponseEntity<List<Recommendation>> recommendations(@RequestParam("logName") String logName, @RequestBody List<Condition> conditions) {
        try {
            var recommendations = logRecommendationService.getRecommendations(logName);
            return ResponseEntity.ok(recommendations);
        } catch (Exception ex) {
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

}
