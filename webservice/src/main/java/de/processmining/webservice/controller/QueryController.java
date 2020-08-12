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

import de.processmining.data.model.Log;
import de.processmining.data.model.Variant;
import de.processmining.data.query.*;
import de.processmining.data.query.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
@RestController
public class QueryController {

    private final QueryService queryService;

    @Autowired
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/query/statistics")
    public Log getStatistics(String logName) {
        return queryService.getLogStatistics(logName);
    }

    @PostMapping("/query/statistics")
    public Log getStatistics(@RequestParam("logName") String logName, @RequestBody List<Condition> conditions) {
        return queryService.getLogStatistics(logName, conditions);
    }

    @GetMapping("/query/get_all_paths")
    public List<Variant> getAllPaths(String logName, @RequestBody List<Condition> conditions) {
        return queryService.getAllPaths(logName, conditions);
    }

    @PostMapping("/query/process_map")
    public ProcessMapResult getProcessMap(@RequestBody ProcessMapQuery query) {
        return queryService.getProcessMap(query);
    }

    @PostMapping("/query/case_attribute_values")
    public CaseAttributeValueResult getCaseAttributeValues(@RequestBody CaseAttributeValueQuery query) {
        return queryService.getCaseAttributeValues(query);
    }

    @PostMapping("/query/drill_down")
    public DrillDownResult getDrillDown(@RequestBody DrillDownQuery query) {
        return queryService.getDrillDown(query);
    }

}