package de.tk.processmining.webservice.controller;

import de.tk.processmining.data.DatabaseConstants;
import de.tk.processmining.data.model.Log;
import de.tk.processmining.data.model.Paths;
import de.tk.processmining.data.model.Variant;
import de.tk.processmining.data.query.QueryManager;
import de.tk.processmining.query.ActivityStat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.Query;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static de.tk.processmining.data.DatabaseConstants.*;

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

    @RequestMapping("dfg/getallpaths")
    public List<Variant> getAllPaths(String logName) {
        return queryManager.getAllPaths(logName);
    }

}