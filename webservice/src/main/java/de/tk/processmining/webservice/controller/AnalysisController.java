package de.tk.processmining.webservice.controller;

import de.tk.processmining.data.analysis.DifferenceAnalysis;
import de.tk.processmining.data.analysis.clustering.MultiPerspectiveTraceClustering;
import de.tk.processmining.data.analysis.clustering.SimpleTraceClustering;
import de.tk.processmining.data.analysis.recommender.RecommendationService;
import de.tk.processmining.data.query.condition.Condition;
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

    @Autowired
    public AnalysisController(SimpleTraceClustering traceClustering, MultiPerspectiveTraceClustering multiPerspectiveTraceClustering, DifferenceAnalysis differenceAnalysis, RecommendationService recommendationService) {
        this.traceClustering = traceClustering;
        this.multiPerspectiveTraceClustering = multiPerspectiveTraceClustering;
        this.differenceAnalysis = differenceAnalysis;
        this.recommendationService = recommendationService;
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
        multiPerspectiveTraceClustering.generateCaseAttributeDb(logName);

        return ResponseEntity.ok().build();
    }

    @RequestMapping("/recommendations")
    public ResponseEntity recommendations(@RequestParam("logName") String logName) {
        var recommendations = recommendationService.getRecommendations(logName);

        return ResponseEntity.ok(recommendations);
    }
}
