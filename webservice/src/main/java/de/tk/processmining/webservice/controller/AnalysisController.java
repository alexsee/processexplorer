package de.tk.processmining.webservice.controller;

import de.tk.processmining.data.analysis.clustering.SimpleTraceClustering;
import de.tk.processmining.data.analysis.metrics.insights.OccurrenceMetric;
import de.tk.processmining.data.query.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
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
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public AnalysisController(SimpleTraceClustering traceClustering, JdbcTemplate jdbcTemplate) {
        this.traceClustering = traceClustering;
        this.jdbcTemplate = jdbcTemplate;
    }

    @RequestMapping("/simple_trace_clustering")
    public ResponseEntity simpleTraceClustering(@RequestParam("logName") String logName) {
        traceClustering.cluster(logName);

        return ResponseEntity.ok().build();
    }

    @RequestMapping("/insights")
    public ResponseEntity insights(@RequestParam("logName") String logName, @RequestBody List<Condition> conditions) {
        var diffAnalysis = new OccurrenceMetric(jdbcTemplate, logName);
        return ResponseEntity.ok(diffAnalysis.getInsights(conditions));
    }

}
