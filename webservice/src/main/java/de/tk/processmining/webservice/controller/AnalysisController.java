package de.tk.processmining.webservice.controller;

import de.tk.processmining.data.analysis.clustering.SimpleTraceClustering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private SimpleTraceClustering traceClustering;

    @Autowired
    public AnalysisController(SimpleTraceClustering traceClustering) {
        this.traceClustering = traceClustering;
    }

    @RequestMapping("/simple_trace_clustering")
    public ResponseEntity simpleTraceClustering(@RequestParam("logName") String logName) {
        traceClustering.cluster(logName);

        return ResponseEntity.ok().build();
    }

}
