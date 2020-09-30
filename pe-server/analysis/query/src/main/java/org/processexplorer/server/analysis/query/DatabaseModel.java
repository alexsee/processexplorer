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

import com.healthmarketscience.sqlbuilder.dbspec.basic.*;

import static org.processexplorer.server.analysis.query.DatabaseConstants.*;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
public class DatabaseModel {

    private final String logName;

    private DbSpec spec = new DbSpec();
    private DbSchema schema = spec.addDefaultSchema();

    // activity table
    public DbTable activityTable;
    public DbColumn activityIdCol;
    public DbColumn activityNameCol;

    // case table
    public DbTable caseTable;
    public DbColumn caseCaseIdCol;
    public DbColumn caseStartTimeCol;
    public DbColumn caseEndTimeCol;
    public DbColumn caseNumEventsCol;
    public DbColumn caseNumUsersCol;
    public DbColumn caseDurationCol;
    public DbColumn caseVariantIdCol;
    public DbColumn caseVariantCol;
    public DbColumn caseResourceVariantCol;
    public DbColumn caseResourceVariantIdCol;

    // events table
    public DbTable eventTable;
    public DbColumn eventCaseIdCol;
    public DbColumn eventEventCol;
    public DbColumn eventTimestampCol;
    public DbColumn eventResourceCol;
    public DbColumn eventLifecycleCol;

    // graph table
    public DbTable graphTable;
    public DbColumn graphCaseIdCol;
    public DbColumn graphSourceCol;
    public DbColumn graphTargetCol;
    public DbColumn graphSourceTimestampCol;
    public DbColumn graphTargetTimestampCol;

    // case attribute table
    public DbTable caseAttributeTable;
    public DbColumn caseAttributeCaseIdCol;
    public DbColumn caseAttributeOriginalCaseIdCol;

    // joints
    public DbJoin caseCaseAttributeJoin;

    public DbJoin graphCaseJoin;
    public DbJoin graphCaseAttributeJoin;

    public DbJoin eventCaseJoin;
    public DbJoin eventCaseAttributeJoin;
    public DbJoin eventActivityJoin;

    public DatabaseModel(String logName) {
        this.logName = logName;

        // activity table
        activityTable = schema.addTable(getActivityTableName(logName));
        activityIdCol = activityTable.addColumn("id", "integer", null);
        activityNameCol = activityTable.addColumn("name", "varchar", 1024);
        activityTable.primaryKey(getActivityTableName(logName) + "_pk", "id");

        // case table
        caseTable = schema.addTable(getCaseTableName(logName));
        caseCaseIdCol = caseTable.addColumn("case_id", "integer", null);
        caseStartTimeCol = caseTable.addColumn("start_time", "timestamp", null);
        caseEndTimeCol = caseTable.addColumn("end_time", "timestamp", null);
        caseNumEventsCol = caseTable.addColumn("num_events", "bigint", null);
        caseNumUsersCol = caseTable.addColumn("num_users", "bigint", null);
        caseDurationCol = caseTable.addColumn("total_duration", "interval", null);
        caseVariantIdCol = caseTable.addColumn("variant_id", "integer", null);
        caseVariantCol = caseTable.addColumn("variant", "text", null);
        caseResourceVariantCol = caseTable.addColumn("resource_variant", "text", null);
        caseResourceVariantIdCol = caseTable.addColumn("resource_variant_id", "integer", null);

        // events table
        eventTable = schema.addTable(getEventsTableName(logName));
        eventCaseIdCol = eventTable.addColumn("case_id", "integer", null);
        eventEventCol = eventTable.addColumn("event", "bigint", null);
        eventTimestampCol = eventTable.addColumn("timestamp", "timestamp", null);
        eventResourceCol = eventTable.addColumn("resource", "varchar", 1024);
        eventLifecycleCol = eventTable.addColumn("lifecycle", "varchar", 1024);

        // graph table
        graphTable = schema.addTable(getGraphTableName(logName));
        graphCaseIdCol = graphTable.addColumn("case_id", "integer", null);
        graphSourceCol = graphTable.addColumn("source", "integer", null);
        graphTargetCol = graphTable.addColumn("target", "integer", null);
        graphSourceTimestampCol = graphTable.addColumn("source_timestamp", "timestamp", null);
        graphTargetTimestampCol = graphTable.addColumn("target_timestamp", "timestamp", null);

        // case attribute table
        caseAttributeTable = schema.addTable(getCaseAttributeTableName(logName));
        caseAttributeCaseIdCol = caseAttributeTable.addColumn("case_id", "integer", null);
        caseAttributeOriginalCaseIdCol = caseAttributeTable.addColumn("original_case_id", "varchar", 1024);

        // joins
        graphCaseJoin = spec.addJoin(null, graphTable.getTableNameSQL(),
                null, caseTable.getTableNameSQL(),
                "case_id");
        graphCaseAttributeJoin = spec.addJoin(null, graphTable.getTableNameSQL(),
                null, caseAttributeTable.getTableNameSQL(),
                "case_id");

        caseCaseAttributeJoin = spec.addJoin(null, caseTable.getTableNameSQL(),
                null, caseAttributeTable.getTableNameSQL(),
                "case_id");

        eventCaseJoin = spec.addJoin(null, eventTable.getTableNameSQL(),
                null, caseTable.getTableNameSQL(),
                "case_id");
        eventCaseAttributeJoin = spec.addJoin(null, eventTable.getTableNameSQL(),
                null, caseAttributeTable.getTableNameSQL(),
                "case_id");
        eventActivityJoin = spec.addJoin(null, eventTable.getTableNameSQL(),
                null, activityTable.getTableNameSQL(),
                new String[]{"event"}, new String[]{"id"});
    }

    public String getGraphTable(String perspective, String nullSource, String nullTarget) {
        return getGraphTable(perspective, nullSource, nullTarget, null);
    }

    public String getGraphTable(String perspective, String nullSource, String nullTarget, String[] filter) {
        String SQL = "WITH " + getGraphTableName(this.logName) + " AS (SELECT " +
                "case_id, " +
                perspective + " AS source, " +
                "COALESCE(LEAD(" + perspective + ", 1) OVER (PARTITION BY case_id ORDER BY timestamp, event), " + nullTarget + ") AS target, " +
                "timestamp AS source_timestamp, " +
                "LEAD(timestamp, 1) OVER (PARTITION BY case_id ORDER BY timestamp, event) AS target_timestamp " +
                "FROM " + this.eventTable.getTableNameSQL() + " ";

        if (filter != null) {
            SQL += "WHERE " + perspective + " IN (" + String.join(", ", filter) + ") ";
        }

        SQL += "UNION ALL " +
                "(SELECT DISTINCT ON (case_id) " +
                "case_id, " +
                nullSource + " as source, " +
                perspective + " as target, " +
                "CAST(null as timestamp) as source_timestamp, " +
                "timestamp as target_timestamp " +
                "FROM " + this.eventTable.getTableNameSQL() + " ";

        if (filter != null) {
            SQL += "WHERE " + perspective + " IN (" + String.join(", ", filter) + ") ";
        }

        SQL += "ORDER BY case_id, target_timestamp, source)) ";

        return SQL;
    }
}
