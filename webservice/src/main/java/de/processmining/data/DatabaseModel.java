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

import com.healthmarketscience.sqlbuilder.dbspec.RejoinTable;
import com.healthmarketscience.sqlbuilder.dbspec.Table;
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
    public DbColumn eventOriginalCaseIdCol;
    public DbColumn eventEventIdCol;
    public DbColumn eventEventNameCol;
    public DbColumn eventResourceCol;
    public DbColumn eventTimestampCol;
    public DbColumn eventLifecycleCol;

    // case attribute table
    public DbTable caseAttributeTable;
    public DbColumn caseAttributeCaseIdCol;
    public DbColumn caseAttributeOriginalCaseIdCol;

    // graph table
    public DbTable graphTable;
    public DbColumn graphEdgeIdCol;
    public DbColumn graphCaseIdCol;
    public DbColumn graphSourceEventCol;
    public DbColumn graphTargetEventCol;
    public DbColumn graphSourceTimestampCol;
    public DbColumn graphTargetTimestampCol;
    public DbColumn graphDurationCol;
    public DbColumn graphVariantIdCol;

    // joints
    public DbJoin caseVariantJoin;
    public DbJoin caseCaseAttributeJoin;

    public DbJoin graphVariantJoin;
    public DbJoin graphCaseAttributeJoin;

    public DbJoin eventCaseJoin;

    public DatabaseModel(String logName) {
        // activity table
        activityTable = schema.addTable(getActivityTableName(logName));
        activityIdCol = activityTable.addColumn("id", "integer", null);
        activityNameCol = activityTable.addColumn("name", "varchar", 250);

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
        caseDurationCol = caseTable.addColumn("duration", "interval", null);
        caseVariantIdCol = caseTable.addColumn("variant_id", "integer", null);

        // events table
        eventTable = schema.addTable(getEventsTableName(logName));
        eventCaseIdCol = eventTable.addColumn("case_id", "integer", null);
        eventOriginalCaseIdCol = eventTable.addColumn("original_case_id", "varchar", 250);
        eventEventIdCol = eventTable.addColumn("event_id", "integer", null);
        eventEventNameCol = eventTable.addColumn("event_name", "varchar", 250);
        eventResourceCol = eventTable.addColumn("resource", "varchar", 250);
        eventTimestampCol = eventTable.addColumn("timestamp", "timestamp", null);
        eventLifecycleCol = eventTable.addColumn("lifecycle", "varchar", null);

        // case attribute table
        caseAttributeTable = schema.addTable(getCaseAttributeTableName(logName));
        caseAttributeCaseIdCol = caseAttributeTable.addColumn("case_id", "integer", null);
        caseAttributeOriginalCaseIdCol = caseAttributeTable.addColumn("original_case_id", "varchar", 250);

        // graph table
        graphTable = schema.addTable(getGraphTableName(logName));
        graphEdgeIdCol = graphTable.addColumn("edge_id", "bigint", null);
        graphCaseIdCol = graphTable.addColumn("case_id", "integer", null);
        graphSourceEventCol = graphTable.addColumn("source_event", "bigint", null);
        graphTargetEventCol = graphTable.addColumn("target_event", "bigint", null);
        graphSourceTimestampCol = graphTable.addColumn("source_timestamp", "timestamp", null);
        graphTargetTimestampCol = graphTable.addColumn("target_timestamp", "timestamp", null);
        graphDurationCol = graphTable.addColumn("duration", "interval", null);
        graphVariantIdCol = graphTable.addColumn("variant_id", "integer", null);

        // joins
        graphVariantJoin = spec.addJoin(null, graphTable.getTableNameSQL(),
                null, variantsTable.getTableNameSQL(),
                new String[]{"variant_id"}, new String[]{"id"});
        graphCaseAttributeJoin = spec.addJoin(null, graphTable.getTableNameSQL(),
                null, caseAttributeTable.getTableNameSQL(),
                "case_id");

        caseVariantJoin = spec.addJoin(null, caseTable.getTableNameSQL(),
                null, variantsTable.getTableNameSQL(),
                new String[]{"variant_id"}, new String[]{"id"});
        caseCaseAttributeJoin = spec.addJoin(null, caseTable.getTableNameSQL(),
                null, caseAttributeTable.getTableNameSQL(),
                "case_id");

        eventCaseJoin = spec.addJoin(null, eventTable.getTableNameSQL(),
                null, caseTable.getTableNameSQL(),
                "case_id");
    }
}
