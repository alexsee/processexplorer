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

package org.processexplorer.server.analysis.query;

import com.healthmarketscience.sqlbuilder.*;
import com.healthmarketscience.sqlbuilder.custom.postgresql.PgExtractDatePart;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import org.processexplorer.server.analysis.query.codes.EventAttributeCodes;
import org.processexplorer.server.analysis.query.condition.Condition;
import org.processexplorer.server.analysis.query.db.PostgresFunctionCall;
import org.processexplorer.server.analysis.query.model.*;
import org.processexplorer.server.analysis.query.request.CaseAttributeValueQuery;
import org.processexplorer.server.analysis.query.request.CasesQuery;
import org.processexplorer.server.analysis.query.request.DrillDownQuery;
import org.processexplorer.server.analysis.query.request.ProcessMapQuery;
import org.processexplorer.server.analysis.query.result.CaseAttributeValueResult;
import org.processexplorer.server.analysis.query.result.DrillDownResult;
import org.processexplorer.server.analysis.query.result.ProcessMapResult;
import org.processexplorer.server.analysis.query.result.SocialNetworkResult;
import org.processexplorer.server.analysis.query.rowmapper.*;
import org.processexplorer.server.analysis.query.selection.SelectionOrder;
import org.processexplorer.server.common.persistence.entity.EventLogAnnotation;
import org.processexplorer.server.common.persistence.repository.EventLogAnnotationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
@Service
public class QueryService {

    private final JdbcTemplate jdbcTemplate;

    private final EventLogAnnotationRepository eventLogAnnotationRepository;

    @Autowired
    public QueryService(JdbcTemplate jdbcTemplate,
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
        return getLogStatistics(logName, null);
    }

    /**
     * Returns basic statistics about a loaded event log.
     *
     * @param logName
     * @return
     */
    public Log getLogStatistics(String logName, List<org.processexplorer.server.analysis.query.condition.Condition> conditions) {
        var db = new DatabaseModel(logName);

        // get activities
        var sqlActivities = new SelectQuery()
                .addColumns(db.activityIdCol)
                .addColumns(db.activityNameCol)
                .addOrdering(db.activityIdCol, OrderObject.Dir.ASCENDING);

        var activities = jdbcTemplate.query(sqlActivities.toString(), new ActivityRowMapper());

        // get resources
        var sqlResources = new SelectQuery(true)
                .addColumns(db.eventResourceCol)
                .addOrdering(db.eventResourceCol, OrderObject.Dir.ASCENDING);

        var resources = jdbcTemplate.queryForList(sqlResources.toString(), String.class);

        // get number of events
        var sqlNumEvents = new SelectQuery()
                .addAliasedColumn(FunctionCall.count().addColumnParams(db.eventCaseIdCol), "num_events")
                .addJoins(SelectQuery.JoinType.LEFT_OUTER, db.eventCaseJoin, db.caseCaseAttributeJoin);

        addConditionsToSql(sqlNumEvents, db, conditions);

        var numEvents = jdbcTemplate.queryForObject(sqlNumEvents.toString(), Long.class);

        // get number of traces
        var sqlNumTraces = new SelectQuery()
                .addAliasedColumn(FunctionCall.count().setIsDistinct(true).addColumnParams(db.caseAttributeCaseIdCol), "num_traces")
                .addJoins(SelectQuery.JoinType.LEFT_OUTER, db.eventCaseJoin, db.caseCaseAttributeJoin);

        addConditionsToSql(sqlNumTraces, db, conditions);

        var numTraces = jdbcTemplate.queryForObject(sqlNumTraces.toString(), Long.class);

        var caseAttributes = getCaseAttributesDetails(logName);
        var eventAttributes = getEventAttributesDetails(logName);

        var result = new Log();
        result.setLogName(logName);
        result.setNumActivities(activities.size());
        result.setActivities(activities);
        result.setResources(resources);
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
    public List<Variant> getAllPaths(String logName, List<org.processexplorer.server.analysis.query.condition.Condition> conditions) {
        var logStats = getLogStatistics(logName);
        var db = new DatabaseModel(logName);

        var sql = new SelectQuery()
                .addColumns(db.caseVariantIdCol, db.caseVariantCol)
                .addAliasedColumn(FunctionCall.count().addColumnParams(db.caseVariantIdCol), "occurrence")
                .addGroupings(db.caseVariantIdCol, db.caseVariantCol)
                .addCustomOrdering("occurrence", OrderObject.Dir.DESCENDING);

        for (var rule : conditions) {
            var condition = rule.getCondition(db);
            if (condition != null) {
                sql.addCondition(condition);
            }
        }

        return jdbcTemplate.query(sql.validate().toString(), new ExtendedVariantRowMapper(logStats));
    }

    /**
     * Returns all existing paths of a loaded event log with corresponding occurrence and variant id.
     *
     * @param db
     * @param conditions
     * @return
     */
    public List<Variant> getAllPathsSimple(DatabaseModel db, DbColumn variantId, List<org.processexplorer.server.analysis.query.condition.Condition> conditions) {

        var sql = new SelectQuery()
                .addAliasedColumn(variantId, "variant_id")
                .addAliasedColumn(FunctionCall.count().addColumnParams(variantId), "occurrence")
                .addJoins(SelectQuery.JoinType.INNER, db.caseCaseAttributeJoin)
                .addGroupings(variantId)
                .addCustomOrdering("occurrence", OrderObject.Dir.DESCENDING);

        for (var rule : conditions) {
            var condition = rule.getCondition(db);
            if (condition != null) {
                sql.addCondition(condition);
            }
        }

        return jdbcTemplate.query(sql.validate().toString(), new VariantRowMapper());
    }

    /**
     * Returns the process map with edges and their duration, occurrence.
     *
     * @param query
     * @return
     */
    public ProcessMapResult getProcessMap(ProcessMapQuery query) {
        var db = new DatabaseModel(query.getLogName());

        var sql = getGraphEdgeQuery(query.getLogName(), db.caseVariantIdCol, query.getConditions());
        var sqlT = db.getGraphTable("event", "-1", "-2", query.getActivityFilter()) + sql.addGroupings(db.graphSourceCol, db.graphTargetCol)
                .addJoins(SelectQuery.JoinType.LEFT_OUTER, db.graphCaseJoin, db.graphCaseAttributeJoin)
                .addCustomOrdering(new CustomSql("occurrence"), OrderObject.Dir.DESCENDING)
                .toString();

        var edges = jdbcTemplate.query(sqlT, new GraphEdgeRowMapper());

        var graph = new Graph();
        graph.setEdges(edges);

        var result = new ProcessMapResult();
        result.setProcessMap(graph);
        result.setVariants(getAllPathsSimple(db, db.caseVariantIdCol, query.getConditions()));

        return result;
    }

    /**
     * Returns the social network graph with edges and their duration, occurrence.
     *
     * @param query
     * @return
     */
    public SocialNetworkResult getSocialNetworkGraph(ProcessMapQuery query) {
        var db = new DatabaseModel(query.getLogName());

        var sql = getGraphEdgeQuery(query.getLogName(), db.caseResourceVariantIdCol, query.getConditions());
        var sqlT = db.getGraphTable("resource", "'start'", "'end'") + sql.addGroupings(db.graphSourceCol, db.graphTargetCol)
                .addJoins(SelectQuery.JoinType.INNER, db.graphCaseJoin, db.graphCaseAttributeJoin)
                .addCustomOrdering(new CustomSql("occurrence"), OrderObject.Dir.DESCENDING)
                .toString();

        var edges = jdbcTemplate.query(sqlT, new SocialNetworkEdgeRowMapper());

        var graph = new SocialNetwork();
        graph.setEdges(edges);

        var result = new SocialNetworkResult();
        result.setSocialNetwork(graph);
        result.setVariants(getAllPathsSimple(db, db.caseResourceVariantIdCol, query.getConditions()));

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
        var db = new DatabaseModel(logName);

        var columns = jdbcTemplate.queryForList("SELECT column_name " +
                        "FROM information_schema.columns " +
                        "WHERE table_name = ? AND table_schema = 'public';",
                String.class,
                db.caseAttributeTable.getTableNameSQL());

        columns.remove("case_id");
        columns.remove("original_case_id");
        columns.remove("concept:name");
        columns.remove("prediction");

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
        var db = new DatabaseModel(logName);

        var columns = jdbcTemplate.queryForList("SELECT column_name " +
                "FROM information_schema.columns " +
                "WHERE table_name = ? AND table_schema = 'public';",
                String.class,
                db.eventTable.getTableNameSQL());

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
            try {
                var sql = new SelectQuery()
                        .addAliasedColumn(new CustomSql("CAST(COUNT(DISTINCT \"" + attr + "\") AS float) / COUNT(\"" + attr + "\")"), "occurrence")
                        .addFromTable(db.caseAttributeTable);

                var result = jdbcTemplate.queryForObject(sql.validate().toString(), Double.class);
                if (result != null && result <= 0.05) {
                    categoricalAttrs.add(attr);
                }
            } catch (Exception ex) {
                // ignore single attribute failures
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
        query.getAttributes().forEach(x -> db.caseAttributeTable.addColumn("\"" + x + "\""));

        var sql = new SelectQuery()
                .addColumns(db.caseCaseIdCol)
                .addColumns(db.caseVariantIdCol);

        // add selected columns
        for (var attr : query.getAttributes()) {
            sql = sql.addColumns(db.caseAttributeTable.findColumn("\"" + attr + "\""));
        }

        sql = sql.addJoins(SelectQuery.JoinType.INNER, db.caseCaseAttributeJoin);

        // add conditions
        for (var rule : query.getConditions()) {
            var condition = rule.getCondition(db);
            if (condition != null) {
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

        // integrated attributes
        if (query.getAttributeName().equals("c_duration")) {
            return new CaseAttributeValueResult("c_duration", "duration");
        } else if (query.getAttributeName().equals("c_starttime") || query.getAttributeName().equals("c_endtime")) {
            return new CaseAttributeValueResult(query.getAttributeName(), "datetime");
        } else if (query.getAttributeName().equals("c_id")) {
            return new CaseAttributeValueResult(query.getAttributeName(), "values");
        }

        // case attribute
        var sql = new SelectQuery(true)
                .addAliasedColumn(db.caseAttributeTable.addColumn("\"" + query.getAttributeName() + "\""), "attr")
                .addFromTable(db.caseTable);

        sql = sql.addJoins(SelectQuery.JoinType.LEFT_OUTER, db.caseCaseAttributeJoin);

        // add conditions
        addConditionsToSql(sql, db, query.getConditions());

        var values = jdbcTemplate.queryForList(sql.validate().toString(), String.class);

        var result = new CaseAttributeValueResult();
        result.setAttributeName(query.getAttributeName());

        if (values.size() > 500) {
            result.setType("value");
        } else {
            result.setValues(values);
            result.setType("list");
        }

        return result;
    }

    /**
     * Returns a list of available cluster indexes.
     *
     * @param logName
     * @return
     */
    public List<Long> getClusterValues(String logName) {
        var db = new DatabaseModel(logName);

        var sql = new SelectQuery(true)
                .addColumns(db.caseAttributeTable.addColumn("cluster_index"))
                .addFromTable(db.caseAttributeTable);

        return jdbcTemplate.queryForList(sql.validate().toString(), Long.class);
    }

    /**
     * Returns a drill down on case attribute data.
     *
     * @param query
     * @return
     */
    public DrillDownResult getDrillDown(DrillDownQuery query) {
        var db = new DatabaseModel(query.getLogName());
        var result = new DrillDownResult();

        var sql = new SelectQuery()
                .addFromTable(db.caseTable)
                .addJoins(SelectQuery.JoinType.INNER, db.caseCaseAttributeJoin);

        // add selections
        int i = 0;
        boolean hasGroup = false;
        for (var selection : query.getSelections()) {
            sql = sql.addAliasedColumn(selection.getSelection(db), "expr" + i);

            // obtain meta data for selected column
            var metaData = new ColumnMetaData(selection.getName(), selection.getType(), selection.getAlias());
            metaData.setCodes(selection.getCodes(eventLogAnnotationRepository, query.getLogName()));
            metaData.setGroup(selection.isGroup());
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
        addConditionsToSql(sql, db, query.getConditions());

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

    /**
     * Returns a list of all activities within the event log.
     *
     * @param logName
     * @return
     */
    public List<Activity> getActivities(String logName) {
        var db = new DatabaseModel(logName);
        var query = new SelectQuery()
                .addColumns(db.activityIdCol, db.activityNameCol)
                .addOrdering(db.activityIdCol, OrderObject.Dir.ASCENDING)
                .toString();

        return jdbcTemplate.query(query, new ActivityRowMapper());
    }

    /**
     * Returns a single case including all attributes and events.
     *
     * @param logName
     * @param caseId
     * @return
     */
    public Case getSingleCase(String logName, long caseId) {
        var db = new DatabaseModel(logName);

        // get case level details
        var caseSQL = new SelectQuery()
                .addAllColumns()
                .addJoins(SelectQuery.JoinType.INNER, db.caseCaseAttributeJoin)
                .addCondition(BinaryCondition.equalTo(db.caseCaseIdCol, caseId));

        var singleCase = jdbcTemplate.queryForObject(caseSQL.validate().toString(), new CaseRowMapper());

        if (singleCase == null) {
            return null;
        }

        // get events
        var eventsSQL = new SelectQuery()
                .addColumns(db.eventCaseIdCol, db.activityIdCol, db.activityNameCol, db.eventResourceCol, db.eventTimestampCol)
                .addJoins(SelectQuery.JoinType.INNER, db.eventActivityJoin)
                .addCondition(BinaryCondition.equalTo(db.eventCaseIdCol, caseId))
                .addOrderings(db.eventTimestampCol, db.eventEventCol);

        singleCase.setEvents(jdbcTemplate.query(eventsSQL.validate().toString(), new EventRowMapper()));

        return singleCase;
    }

    /**
     * Adds conditions to an existing select query object.
     *
     * @param sql
     * @param db
     * @param conditions
     */
    private void addConditionsToSql(SelectQuery sql, DatabaseModel db, List<Condition> conditions) {
        // conditions available?
        if (conditions == null) {
            return;
        }

        // add conditions
        for (var rule : conditions) {
            var condition = rule.getCondition(db);
            if (condition != null) {
                sql.addCondition(condition);
            }
        }
    }

    /**
     * Returns a select query object for obtaining general statistics about the graph edges.
     *
     * @param logName
     * @param conditions
     * @return
     */
    private SelectQuery getGraphEdgeQuery(String logName, DbColumn variant, List<Condition> conditions) {
        var db = new DatabaseModel(logName);

        var sql = new SelectQuery()
                .addColumns(db.graphSourceCol, db.graphTargetCol)
                .addAliasedColumn(new ExtractExpression(PgExtractDatePart.EPOCH, FunctionCall.avg().addCustomParams(PostgresFunctionCall.age().addCustomParams(db.graphTargetTimestampCol, db.graphSourceTimestampCol))), "avg_duration")
                .addAliasedColumn(new ExtractExpression(PgExtractDatePart.EPOCH, FunctionCall.min().addCustomParams(PostgresFunctionCall.age().addCustomParams(db.graphTargetTimestampCol, db.graphSourceTimestampCol))), "min_duration")
                .addAliasedColumn(new ExtractExpression(PgExtractDatePart.EPOCH, FunctionCall.max().addCustomParams(PostgresFunctionCall.age().addCustomParams(db.graphTargetTimestampCol, db.graphSourceTimestampCol))), "max_duration")
                .addAliasedColumn(FunctionCall.countAll(), "occurrence")
                .addAliasedColumn(new CustomSql("string_agg(distinct cast(" + variant.getColumnNameSQL() + " as text), ',')"), "variants");

        // add conditions
        addConditionsToSql(sql, db, conditions);

        return sql;
    }
}
