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

package de.processmining.webservice.controller;

import de.processmining.data.analysis.ArtifactAnalysis;
import de.processmining.data.analysis.DifferenceAnalysis;
import de.processmining.data.analysis.clustering.MultiPerspectiveTraceClustering;
import de.processmining.data.analysis.clustering.SimpleTraceClustering;
import de.processmining.data.analysis.recommender.RecommendationService;
import de.processmining.data.query.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    private RecommendationService recommendationService;

    private ArtifactAnalysis artifactAnalysis;

    @Autowired
    public AnalysisController(SimpleTraceClustering traceClustering, MultiPerspectiveTraceClustering multiPerspectiveTraceClustering, DifferenceAnalysis differenceAnalysis, RecommendationService recommendationService, ArtifactAnalysis artifactAnalysis) {
        this.traceClustering = traceClustering;
        this.multiPerspectiveTraceClustering = multiPerspectiveTraceClustering;
        this.differenceAnalysis = differenceAnalysis;
        this.recommendationService = recommendationService;
        this.artifactAnalysis = artifactAnalysis;
    }

    @RequestMapping("/simple_trace_clustering")
    public ResponseEntity simpleTraceClustering(@RequestParam("logName") String logName) {
        traceClustering.cluster(logName);

        return ResponseEntity.ok().build();
    }

    @RequestMapping("/insights")
    public ResponseEntity insights(@RequestParam("logName") String logName, @RequestBody List<Condition> conditions) {
        var diffAnalysis = differenceAnalysis.getInsights(differenceAnalysis.getDefaultMetrics(logName), conditions);
        return ResponseEntity.ok(diffAnalysis);
    }

    @RequestMapping("/multi_trace_clustering")
    public ResponseEntity multiTraceClustering(@RequestParam("logName") String logName) {
        multiPerspectiveTraceClustering.cluster(logName);

        return ResponseEntity.ok().build();
    }

    @RequestMapping("/recommendations")
    public ResponseEntity recommendations(@RequestParam("logName") String logName, @RequestBody List<Condition> conditions) {
        var recommendations = recommendationService.getRecommendations(logName);

        return ResponseEntity.ok(recommendations);
    }

    @RequestMapping("/artifacts")
    public ResponseEntity artifacts(@RequestParam("logName") String logName) {
        return ResponseEntity.ok(artifactAnalysis.run(logName));
    }
}
