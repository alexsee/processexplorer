package de.tk.processmining.data.query;

import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.custom.postgresql.PgExtractDatePart;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.model.Graph;
import de.tk.processmining.data.model.GraphEdge;
import de.tk.processmining.data.model.Log;
import de.tk.processmining.data.model.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static de.tk.processmining.data.DatabaseConstants.getCaseAttributeTableName;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
@Service
public class QueryManager {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public QueryManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Returns basic statistics about a loaded event log.
     *
     * @param logName
     * @return
     */
    public Log getLogStatistics(String logName) {
        var db = new DatabaseModel(logName);

        var activities = jdbcTemplate.queryForList(new SelectQuery().addColumns(db.activityNameCol).addOrdering(db.activityIdCol, OrderObject.Dir.ASCENDING).toString(), String.class);
        var numEvents = jdbcTemplate.queryForObject(new SelectQuery().addAliasedColumn(FunctionCall.count().addColumnParams(db.eventCaseIdCol), "num_events").toString(), Long.class);
        var numTraces = jdbcTemplate.queryForObject(new SelectQuery().addAliasedColumn(FunctionCall.count().addColumnParams(db.caseAttributeCaseIdCol), "num_traces").toString(), Long.class);
        var attributes = getCaseAttributes(logName);

        var result = new Log();
        result.setLogName(logName);
        result.setNumActivities(activities.size());
        result.setActivities(activities);
        result.setNumEvents(numEvents);
        result.setNumTraces(numTraces);
        result.setAttributes(attributes);

        return result;
    }

    /**
     * Returns all existing paths of a loaded event log with corresponding occurrence and variant id.
     *
     * @param logName
     * @return
     */
    public List<Variant> getAllPaths(String logName) {
        var logStats = getLogStatistics(logName);
        var db = new DatabaseModel(logName);

        var sql = new SelectQuery()
                .addAllTableColumns(db.variantsTable)
                .addAliasedColumn(FunctionCall.count().addColumnParams(db.caseVariantIdCol), "occurrence")
                .addJoins(SelectQuery.JoinType.INNER, db.caseVariantJoin)
                .addGroupings(db.variantsIdCol)
                .addCustomOrdering("occurrence", OrderObject.Dir.DESCENDING)
                .validate().toString();

        var rowMapper = new RowMapper<Variant>() {
            public Variant mapRow(ResultSet rs, int rowNum) throws SQLException {
                var result = new Variant();
                result.setId(rs.getLong(1));
                result.setOccurrence(rs.getLong(3));

                var path = rs.getString(2).split("::");
                var path_index = new int[path.length];

                for (int i = 0; i < path.length; i++) {
                    var index = Integer.parseInt(path[i].replace(":", ""));

                    path[i] = logStats.getActivities().get(index);
                    path_index[i] = index;
                }

                result.setPath(path);
                result.setPathIndex(path_index);
                return result;
            }
        };

        return jdbcTemplate.query(sql, rowMapper);
    }

    /**
     * Returns the process map with edges and their duration, occurrence.
     *
     * @param logName
     * @return
     */
    public Graph getProcessMap(String logName, List<de.tk.processmining.data.query.condition.Condition> conditions) {
        var db = new DatabaseModel(logName);

        var sql = new SelectQuery()
                .addColumns(db.graphSourceEventCol, db.graphTargetEventCol)
                .addAliasedColumn(new ExtractExpression(PgExtractDatePart.EPOCH, FunctionCall.avg().addColumnParams(db.graphDurationCol)), "avg_duration")
                .addAliasedColumn(new ExtractExpression(PgExtractDatePart.EPOCH, FunctionCall.min().addColumnParams(db.graphDurationCol)), "min_duration")
                .addAliasedColumn(new ExtractExpression(PgExtractDatePart.EPOCH, FunctionCall.max().addColumnParams(db.graphDurationCol)), "max_duration")
                .addAliasedColumn(FunctionCall.countAll(), "occurrence");

        for (var rule : conditions) {
            for (var condition : rule.getCondition(db)) {
                sql.addCondition(condition);
            }
        }

        var sqlT = sql.addGroupings(db.graphSourceEventCol, db.graphTargetEventCol)
                .addJoins(SelectQuery.JoinType.INNER, db.graphVariantJoin, db.graphCaseAttributeJoin)
                .validate().toString();

        var rowMapper = new RowMapper<GraphEdge>() {
            public GraphEdge mapRow(ResultSet rs, int rowNum) throws SQLException {
                var result = new GraphEdge();
                result.setSourceEvent(rs.getString(1));
                result.setTargetEvent(rs.getString(2));
                result.setAvgDuration(rs.getLong(3));
                result.setMinDuration(rs.getLong(4));
                result.setMaxDuration(rs.getLong(5));
                result.setOccurrence(rs.getLong(6));

                return result;
            }
        };

        var edges = jdbcTemplate.query(sqlT, rowMapper);

        var graph = new Graph();
        graph.setEdges(edges);

        return graph;
    }

    public List<String> getCaseAttributes(String logName) {
        var columns = jdbcTemplate.queryForList("SELECT column_name " +
                "FROM information_schema.columns " +
                "WHERE table_name = '" + getCaseAttributeTableName(logName) + "' AND table_schema = 'public';", String.class);

        columns.remove("case_id");
        columns.remove("original_case_id");
        columns.remove("concept:name");

        return columns;
    }

    public List<String> getCategoricalCaseAttributes(String logName) {
        var db = new DatabaseModel(logName);
        var attrs = getCaseAttributes(logName);

        var categoricalAttrs = new ArrayList<String>();

        for (var attr : attrs) {
            var sql = new SelectQuery()
                    .addAliasedColumn(new CustomSql("CAST(COUNT(DISTINCT \"" + attr + "\") AS float) / COUNT(\"" + attr + "\")"), "occurrence")
                    .addFromTable(db.caseAttributeTable);

            var result = jdbcTemplate.queryForObject(sql.validate().toString(), Double.class);
            if (result != null && result <= 0.01) {
                categoricalAttrs.add(attr);
            }
        }

        return categoricalAttrs;
    }

    public List<Map<String, Object>> getCases(String logName, List<String> attributes) {
        return getCases(logName, attributes, new ArrayList<>());
    }

    public List<Map<String, Object>> getCases(String logName, List<String> attributes, List<de.tk.processmining.data.query.condition.Condition> conditions) {
        var db = new DatabaseModel(logName);
        attributes.forEach(x -> db.caseAttributeTable.addColumn(x));

        var sql = new SelectQuery()
                .addColumns(db.caseCaseIdCol)
                .addColumns(db.caseVariantIdCol);

        // add selected columns
        for (var attr : attributes) {
            sql = sql.addColumns(db.caseAttributeTable.findColumn(attr));
        }

        sql = sql.addJoins(SelectQuery.JoinType.INNER, db.caseCaseAttributeJoin);

        // add conditions
        for (var rule : conditions) {
            for (var condition : rule.getCondition(db)) {
                sql.addCondition(condition);
            }
        }

        return jdbcTemplate.queryForList(sql.validate().toString());
    }
}
