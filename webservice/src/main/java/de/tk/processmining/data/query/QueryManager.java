package de.tk.processmining.data.query;

import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.custom.postgresql.PgExtractDatePart;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.analysis.categorization.EventAttributeCodes;
import de.tk.processmining.data.model.*;
import de.tk.processmining.data.query.selection.SelectionOrder;
import de.tk.processmining.webservice.database.EventLogAnnotationRepository;
import de.tk.processmining.webservice.database.entities.EventLogAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static de.tk.processmining.data.DatabaseConstants.getCaseAttributeTableName;
import static de.tk.processmining.data.DatabaseConstants.getEventsTableName;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
@Service
public class QueryManager {

    private JdbcTemplate jdbcTemplate;

    private EventLogAnnotationRepository eventLogAnnotationRepository;

    @Autowired
    public QueryManager(JdbcTemplate jdbcTemplate,
                        EventLogAnnotationRepository eventLogAnnotationRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventLogAnnotationRepository = eventLogAnnotationRepository;
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

        var caseAttributes = getCaseAttributesDetails(logName);
        var eventAttributes = getEventAttributesDetails(logName);

        var result = new Log();
        result.setLogName(logName);
        result.setNumActivities(activities.size());
        result.setActivities(activities);
        result.setNumEvents(numEvents);
        result.setNumTraces(numTraces);

        result.setCaseAttributes(caseAttributes);
        result.setEventAttributes(eventAttributes);

        return result;
    }

    /**
     * Returns all existing paths of a loaded event log with corresponding occurrence and variant id.
     *
     * @param logName
     * @param conditions
     * @return
     */
    public List<Variant> getAllPaths(String logName, List<de.tk.processmining.data.query.condition.Condition> conditions) {
        var logStats = getLogStatistics(logName);
        var db = new DatabaseModel(logName);

        var sql = new SelectQuery()
                .addAllTableColumns(db.variantsTable)
                .addAliasedColumn(FunctionCall.count().addColumnParams(db.caseVariantIdCol), "occurrence")
                .addJoins(SelectQuery.JoinType.INNER, db.caseVariantJoin)
                .addGroupings(db.variantsIdCol)
                .addCustomOrdering("occurrence", OrderObject.Dir.DESCENDING);

        for (var rule : conditions) {
            for (var condition : rule.getCondition(db)) {
                sql.addCondition(condition);
            }
        }

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

        return jdbcTemplate.query(sql.validate().toString(), rowMapper);
    }

    /**
     * Returns the process map with edges and their duration, occurrence.
     *
     * @param query
     * @return
     */
    public ProcessMapResult getProcessMap(ProcessMapQuery query) {
        var db = new DatabaseModel(query.getLogName());

        var sql = new SelectQuery()
                .addColumns(db.graphSourceEventCol, db.graphTargetEventCol)
                .addAliasedColumn(new ExtractExpression(PgExtractDatePart.EPOCH, FunctionCall.avg().addColumnParams(db.graphDurationCol)), "avg_duration")
                .addAliasedColumn(new ExtractExpression(PgExtractDatePart.EPOCH, FunctionCall.min().addColumnParams(db.graphDurationCol)), "min_duration")
                .addAliasedColumn(new ExtractExpression(PgExtractDatePart.EPOCH, FunctionCall.max().addColumnParams(db.graphDurationCol)), "max_duration")
                .addAliasedColumn(FunctionCall.countAll(), "occurrence");

        for (var rule : query.getConditions()) {
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

        var result = new ProcessMapResult();
        result.setProcessMap(graph);
        return result;
    }

    /**
     * Returns a detailed list of all available case attributes.
     *
     * @param logName
     * @return
     */
    public List<ColumnMetaData> getCaseAttributesDetails(String logName) {
        var columns = getCaseAttributes(logName);

        var result = new ArrayList<ColumnMetaData>();
        for (var column : columns) {
            var annotations = eventLogAnnotationRepository.findByLogNameAndColumnTypeAndColumnName(logName, "case_attribute", column);
            var codes = annotations.stream().map(EventLogAnnotation::getCode).map(EventAttributeCodes::valueOf).collect(Collectors.toList());

            result.add(new ColumnMetaData(column, "case_attribute", null, codes));
        }

        return result;
    }

    /**
     * Returns a list of all available case attributes.
     *
     * @param logName
     * @return
     */
    public List<String> getCaseAttributes(String logName) {
        var columns = jdbcTemplate.queryForList("SELECT column_name " +
                "FROM information_schema.columns " +
                "WHERE table_name = '" + getCaseAttributeTableName(logName) + "' AND table_schema = 'public';", String.class);

        columns.remove("case_id");
        columns.remove("original_case_id");
        columns.remove("concept:name");

        return columns;
    }

    /**
     * Returns a detailed list of all available event attributes.
     *
     * @param logName
     * @return
     */
    public List<ColumnMetaData> getEventAttributesDetails(String logName) {
        var columns = getEventAttributes(logName);

        var result = new ArrayList<ColumnMetaData>();
        for (var column : columns) {
            var annotations = eventLogAnnotationRepository.findByLogNameAndColumnTypeAndColumnName(logName, "case_attribute", column);
            var codes = annotations.stream().map(EventLogAnnotation::getCode).map(EventAttributeCodes::valueOf).collect(Collectors.toList());

            result.add(new ColumnMetaData(column, "event_attribute", null, codes));
        }

        return result;
    }

    /**
     * Returns a list of all available event attributes.
     *
     * @param logName
     * @return
     */
    public List<String> getEventAttributes(String logName) {
        var columns = jdbcTemplate.queryForList("SELECT column_name " +
                "FROM information_schema.columns " +
                "WHERE table_name = '" + getEventsTableName(logName) + "' AND table_schema = 'public';", String.class);

        columns.remove("case_id");
        columns.remove("original_case_id");
        columns.remove("event_id");

        return columns;
    }

    /**
     * Returns a list of all categorical case attributes.
     *
     * @param logName
     * @return
     */
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

    /**
     * Returns a list of all cases with the given condition and the corresponding case attributes.
     *
     * @param query
     * @return
     */
    public List<Map<String, Object>> getCases(CasesQuery query) {
        var db = new DatabaseModel(query.getLogName());
        query.getAttributes().forEach(x -> db.caseAttributeTable.addColumn(x));

        var sql = new SelectQuery()
                .addColumns(db.caseCaseIdCol)
                .addColumns(db.caseVariantIdCol);

        // add selected columns
        for (var attr : query.getAttributes()) {
            sql = sql.addColumns(db.caseAttributeTable.findColumn(attr));
        }

        sql = sql.addJoins(SelectQuery.JoinType.INNER, db.caseCaseAttributeJoin);

        // add conditions
        for (var rule : query.getConditions()) {
            for (var condition : rule.getCondition(db)) {
                sql.addCondition(condition);
            }
        }

        return jdbcTemplate.queryForList(sql.validate().toString());
    }

    /**
     * Returns a list of values that correspond to the given case attribute. If the attribute is not a categorical
     * attribute, the function will indicate that.
     *
     * @param query
     * @return
     */
    public CaseAttributeValueResult getCaseAttributeValues(CaseAttributeValueQuery query) {
        var db = new DatabaseModel(query.getLogName());

        var sql = new SelectQuery(true)
                .addAliasedColumn(db.caseAttributeTable.addColumn("\"" + query.getAttributeName() + "\""), "attr")
                .addFromTable(db.caseTable);

        sql = sql.addJoins(SelectQuery.JoinType.INNER, db.caseCaseAttributeJoin, db.caseVariantJoin);

        // add conditions
        for (var rule : query.getConditions()) {
            for (var condition : rule.getCondition(db)) {
                sql.addCondition(condition);
            }
        }

        var values = jdbcTemplate.queryForList(sql.validate().toString(), String.class);

        var result = new CaseAttributeValueResult();
        result.setAttributeName(query.getAttributeName());

        if (values.size() > 100) {
            result.setCategorical(false);
        } else {
            result.setValues(values);
            result.setCategorical(true);
        }

        return result;
    }

    public DrillDownResult getDrillDown(DrillDownQuery query) {
        var db = new DatabaseModel(query.getLogName());
        var result = new DrillDownResult();

        var sql = new SelectQuery()
                .addFromTable(db.caseTable)
                .addJoins(SelectQuery.JoinType.INNER, db.caseCaseAttributeJoin, db.caseVariantJoin);

        // add selections
        int i = 0;
        boolean hasGroup = false;
        for (var selection : query.getSelections()) {
            sql = sql.addAliasedColumn(selection.getSelection(db), "expr" + i);

            // obtain meta data for selected column
            var metaData = new ColumnMetaData(selection.getName(), "", selection.getAlias());
            metaData.setCodes(selection.getCodes(eventLogAnnotationRepository, query.getLogName()));
            result.getMetaData().add(metaData);

            if (selection.isGroup()) {
                hasGroup = true;
            }

            if (selection.getOrdering() != null) {
                sql = sql.addCustomOrdering(new CustomSql("expr" + i), selection.getOrdering() == SelectionOrder.ASC ? OrderObject.Dir.ASCENDING : OrderObject.Dir.DESCENDING);
            }

            i++;
        }

        // add grouping?
        if (hasGroup) {
            for (var selection : query.getSelections()) {
                if (selection.isGroup())
                    continue;

                sql = sql.addCustomGroupings(selection.getSelection(db));
            }
        }

        // add conditions
        for (var rule : query.getConditions()) {
            for (var condition : rule.getCondition(db)) {
                sql = sql.addCondition(condition);
            }
        }

        var values = jdbcTemplate.queryForList(sql.validate().toString());

        for (var value : values) {
            var data = new Object[value.size()];

            for (i = 0; i < query.getSelections().size(); i++) {
                data[i] = value.get("expr" + i);
            }

            result.getData().add(data);
        }

        return result;
    }
}
