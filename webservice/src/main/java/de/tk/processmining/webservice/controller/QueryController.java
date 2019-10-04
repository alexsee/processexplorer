package de.tk.processmining.webservice.controller;

import de.tk.processmining.data.model.Graph;
import de.tk.processmining.data.model.Log;
import de.tk.processmining.data.model.Variant;
import de.tk.processmining.data.query.QueryManager;
import de.tk.processmining.data.query.condition.Condition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

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

    @RequestMapping("/statistics")
    public Log getStatistics(String logName) {
        return queryManager.getLogStatistics(logName);
    }

    @RequestMapping("getallpaths")
    public List<Variant> getAllPaths(String logName, @RequestBody List<Condition> conditions) {
        return queryManager.getAllPaths(logName, conditions);
    }

    @RequestMapping(value = "/getprocessmap", method = RequestMethod.POST)
    public Graph getProcessMap(@RequestParam String logName, @RequestBody List<Condition> conditions) {
        return queryManager.getProcessMap(logName, conditions);
    }

}