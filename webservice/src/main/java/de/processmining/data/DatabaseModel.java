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

package de.processmining.data;

import com.healthmarketscience.sqlbuilder.dbspec.basic.*;

import static de.processmining.data.DatabaseConstants.*;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
public class DatabaseModel {

    private DbSpec spec = new DbSpec();
    private DbSchema schema = spec.addDefaultSchema();

    // activity table
    public DbTable activityTable;
    public DbColumn activityIdCol;
    public DbColumn activityNameCol;

    // variants table
    public DbTable variantsTable;
    public DbColumn variantsIdCol;
    public DbColumn variantsVariantCol;

    // case table
    public DbTable caseTable;
    public DbColumn caseCaseIdCol;
    public DbColumn caseStartTimeCol;
    public DbColumn caseEndTimeCol;
    public DbColumn caseNumEventsCol;
    public DbColumn caseNumUsersCol;
    public DbColumn caseDurationCol;
    public DbColumn caseVariantIdCol;

    // events table
    public DbTable eventTable;
    public DbColumn eventCaseIdCol;
    public DbColumn eventEdgeIdCol;
    public DbColumn eventOriginalCaseIdCol;
    public DbColumn eventSourceEventCol;
    public DbColumn eventTargetEventCol;
    public DbColumn eventSourceTimestampCol;
    public DbColumn eventTargetTimestampCol;
    public DbColumn eventSourceResourceCol;
    public DbColumn eventTargetResourceCol;
    public DbColumn eventDurationCol;
    public DbColumn eventNumberCol;
    public DbColumn eventLifecycleCol;

    // case attribute table
    public DbTable caseAttributeTable;
    public DbColumn caseAttributeCaseIdCol;
    public DbColumn caseAttributeOriginalCaseIdCol;

    // joints
    public DbJoin caseVariantJoin;
    public DbJoin caseCaseAttributeJoin;

    public DbJoin eventCaseJoin;
    public DbJoin eventCaseAttributeJoin;
    public DbJoin eventActivityJoin;

    public DatabaseModel(String logName) {
        // activity table
        activityTable = schema.addTable(getActivityTableName(logName));
        activityIdCol = activityTable.addColumn("id", "integer", null);
        activityNameCol = activityTable.addColumn("name", "varchar", 1024);
        activityTable.primaryKey(getActivityTableName(logName) + "_pk", "id");

        // variants table
        variantsTable = schema.addTable(getVariantsTableName(logName));
        variantsIdCol = variantsTable.addColumn("id", "integer", null);
        variantsVariantCol = variantsTable.addColumn("variant", "text", null);

        // case table
        caseTable = schema.addTable(getCaseTableName(logName));
        caseCaseIdCol = caseTable.addColumn("case_id", "integer", null);
        caseStartTimeCol = caseTable.addColumn("start_time", "timestamp", null);
        caseEndTimeCol = caseTable.addColumn("end_time", "timestamp", null);
        caseNumEventsCol = caseTable.addColumn("num_events", "bigint", null);
        caseNumUsersCol = caseTable.addColumn("num_users", "bigint", null);
        caseDurationCol = caseTable.addColumn("total_duration", "interval", null);
        caseVariantIdCol = caseTable.addColumn("variant_id", "integer", null);

        // events table
        eventTable = schema.addTable(getEventsTableName(logName));
        eventCaseIdCol = eventTable.addColumn("case_id", "integer", null);
        eventOriginalCaseIdCol = eventTable.addColumn("original_case_id", "varchar", 1024);
        eventSourceEventCol = eventTable.addColumn("source_event", "bigint", null);
        eventTargetEventCol = eventTable.addColumn("target_event", "bigint", null);
        eventSourceTimestampCol = eventTable.addColumn("source_timestamp", "timestamp", null);
        eventTargetTimestampCol = eventTable.addColumn("target_timestamp", "timestamp", null);
        eventSourceResourceCol = eventTable.addColumn("source_resource", "varchar", 1024);
        eventTargetResourceCol = eventTable.addColumn("target_resource", "varchar", 1024);
        eventDurationCol = eventTable.addColumn("duration", "interval", null);
        eventNumberCol = eventTable.addColumn("event_number", "integer", null);
        eventLifecycleCol = eventTable.addColumn("lifecycle", "varchar", 1024);

        // case attribute table
        caseAttributeTable = schema.addTable(getCaseAttributeTableName(logName));
        caseAttributeCaseIdCol = caseAttributeTable.addColumn("case_id", "integer", null);
        caseAttributeOriginalCaseIdCol = caseAttributeTable.addColumn("original_case_id", "varchar", 1024);

        // joins
        caseVariantJoin = spec.addJoin(null, caseTable.getTableNameSQL(),
                null, variantsTable.getTableNameSQL(),
                new String[]{"variant_id"}, new String[]{"id"});
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
                new String[]{"source_event"}, new String[]{"id"});
    }
}
