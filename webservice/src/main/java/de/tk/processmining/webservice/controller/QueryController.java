package de.tk.processmining.webservice.controller;

import de.tk.processmining.data.model.Log;
import de.tk.processmining.data.model.Variant;
import de.tk.processmining.data.query.*;
import de.tk.processmining.data.query.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
@RestController
public class QueryController {

    private final QueryManager queryManager;

    @Autowired
    public QueryController(QueryManager queryManager) {
        this.queryManager = queryManager;
    }

    @RequestMapping("/query/statistics")
    public Log getStatistics(String logName) {
        return queryManager.getLogStatistics(logName);
    }

    @RequestMapping("/query/get_all_paths")
    public List<Variant> getAllPaths(String logName, @RequestBody List<Condition> conditions) {
        return queryManager.getAllPaths(logName, conditions);
    }

    @RequestMapping(value = "/query/process_map", method = RequestMethod.POST)
    public ProcessMapResult getProcessMap(@RequestBody ProcessMapQuery query) {
        return queryManager.getProcessMap(query);
    }

    @RequestMapping(value = "/query/case_attribute_values", method = RequestMethod.POST)
    public CaseAttributeValueResult getCaseAttributeValues(@RequestBody CaseAttributeValueQuery query) {
        return queryManager.getCaseAttributeValues(query);
    }

    @RequestMapping(value = "/query/drill_down", method = RequestMethod.POST)
    public DrillDownResult getDrillDown(@RequestBody DrillDownQuery query) {
        return queryManager.getDrillDown(query);
    }

}