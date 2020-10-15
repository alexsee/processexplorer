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

import org.processexplorer.server.analysis.ml.simulation.SensitivityAnalysis;
import org.processexplorer.server.analysis.ml.simulation.SensitivityResult;
import org.processexplorer.server.analysis.ml.simulation.SensitivityValue;
import org.processexplorer.server.analysis.query.QueryService;
import org.processexplorer.server.analysis.query.condition.Condition;
import org.processexplorer.server.analysis.query.model.Case;
import org.processexplorer.server.analysis.query.model.Log;
import org.processexplorer.server.analysis.query.model.Variant;
import org.processexplorer.server.analysis.query.request.CaseAttributeValueQuery;
import org.processexplorer.server.analysis.query.request.DrillDownQuery;
import org.processexplorer.server.analysis.query.request.ProcessMapQuery;
import org.processexplorer.server.analysis.query.result.CaseAttributeValueResult;
import org.processexplorer.server.analysis.query.result.DrillDownResult;
import org.processexplorer.server.analysis.query.result.ProcessMapResult;
import org.processexplorer.server.analysis.query.result.SocialNetworkResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
@RestController
public class QueryController {

    private final QueryService queryService;
    private final SensitivityAnalysis sensitivityAnalysis;

    @Autowired
    public QueryController(QueryService queryService, SensitivityAnalysis sensitivityAnalysis) {
        this.queryService = queryService;
        this.sensitivityAnalysis = sensitivityAnalysis;
    }

    @GetMapping("/query/statistics")
    public ResponseEntity<Log> getStatistics(String logName) {
        return ResponseEntity.ok(queryService.getLogStatistics(logName));
    }

    @PostMapping("/query/statistics")
    public ResponseEntity<Log> getStatistics(@RequestParam("logName") String logName, @RequestBody List<Condition> conditions) {
        return ResponseEntity.ok(queryService.getLogStatistics(logName, conditions));
    }

    @GetMapping("/query/get_all_paths")
    public ResponseEntity<List<Variant>> getAllPaths(String logName, @RequestBody List<Condition> conditions) {
        return ResponseEntity.ok(queryService.getAllPaths(logName, conditions));
    }

    @PostMapping("/query/process_map")
    public ResponseEntity<ProcessMapResult> getProcessMap(@RequestBody ProcessMapQuery query) {
        return ResponseEntity.ok(queryService.getProcessMap(query));
    }

    @PostMapping("/query/social_network")
    public ResponseEntity<SocialNetworkResult> getSocialNetworkGraph(@RequestBody ProcessMapQuery query) {
        return ResponseEntity.ok(queryService.getSocialNetworkGraph(query));
    }

    @PostMapping("/query/case_attribute_values")
    public ResponseEntity<CaseAttributeValueResult> getCaseAttributeValues(@RequestBody CaseAttributeValueQuery query) {
        return ResponseEntity.ok(queryService.getCaseAttributeValues(query));
    }

    @PostMapping("/query/drill_down")
    public ResponseEntity<DrillDownResult> getDrillDown(@RequestBody DrillDownQuery query) {
        return ResponseEntity.ok(queryService.getDrillDown(query));
    }

    @PostMapping("/query/sensitivity_analysis")
    public ResponseEntity<Map<String, SensitivityResult>> getDrillDownExtended(@RequestBody DrillDownQuery query) {
        return ResponseEntity.ok(sensitivityAnalysis.simulate(query));
    }

    @GetMapping("/query/case")
    public ResponseEntity<Case> getCase(@RequestParam("logName") String logName, @RequestParam("id") long caseId) {
        var singleCase = queryService.getSingleCase(logName, caseId);
        if (singleCase == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(singleCase);
    }
}