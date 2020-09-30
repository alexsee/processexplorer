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

package org.processexplorer.server.analysis.mining.log;

import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import org.apache.commons.lang3.StringUtils;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.*;
import org.processexplorer.server.analysis.query.DatabaseModel;
import org.processexplorer.server.common.utils.OutputBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.processexplorer.server.analysis.query.DatabaseConstants.*;

/**
 * @author Alexander Seeliger on 20.09.2019.
 */
public class XLog2Database {

    private static final Logger logger = LoggerFactory.getLogger(XLog2Database.class);

    private static final int BUFFER_SIZE = 1000;

    private final String logName;

    private final XEventClassifier classifier = new XEventNameClassifier();

    private final JdbcTemplate jdbcTemplate;

    private final DatabaseModel db;

    public XLog2Database(JdbcTemplate jdbcTemplate, String logName) {
        this.logName = logName;
        this.jdbcTemplate = jdbcTemplate;

        this.db = new DatabaseModel(logName);
    }

    public boolean importLog(XLog log) {
        logger.info("Begin importing event log \"{}\"", this.logName);

        // read general log properties
        var logInfo = XLogInfoFactory.createLogInfo(log);
        var traceAttributes = new ArrayList<>(logInfo.getTraceAttributeInfo().getAttributes());
        var eventAttributes = getEventAttributes(new ArrayList<>(logInfo.getEventAttributeInfo().getAttributes()));

        // generate tables
        generateActivitiesTable(logInfo);
        generateCaseAttributeTable(traceAttributes);
        generateEventsTable(eventAttributes);

        String insertEventSql = new InsertQuery(db.eventTable)
                .addPreparedColumnCollection(db.eventTable.getColumns())
                .validate().toString();
        String insertTraceSql = "INSERT INTO " + getCaseAttributeTableName(this.logName) + " VALUES (" + StringUtils.repeat("?", ",", traceAttributes.size() + 2) + ");";

        try {
            var prepInsertEvent = new ArrayList<Object[]>();
            var prepInsertTrace = new ArrayList<Object[]>();

            // import events and cases
            int buffer = 0;
            for (int i = 0; i < log.size(); i++) {
                var trace = log.get(i);

                var caseId = XLogUtils.getAttributeValue(trace.getAttributes().get(XConceptExtension.KEY_NAME)).toString();

                // obtain trace attributes
                var tracePrep = new Object[2 + traceAttributes.size()];
                tracePrep[0] = i;
                tracePrep[1] = caseId;

                for (int j = 0; j < traceAttributes.size(); j++) {
                    var value = XLogUtils.getAttributeValue(trace.getAttributes().get(traceAttributes.get(j).getKey()));

                    if (value instanceof Date) {
                        tracePrep[2 + j] = new java.sql.Timestamp(((Date) value).getTime());
                    } else {
                        tracePrep[2 + j] = value;
                    }
                }
                prepInsertTrace.add(tracePrep);

                // obtain events for trace
                for (var event : trace) {
                    var timestamp = (Date) XLogUtils.getAttributeValue(event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP));
                    var eventPrep = new Object[5 + eventAttributes.size()];

                    eventPrep[0] = i;
                    eventPrep[1] = logInfo.getEventClasses().getClassOf(event).getIndex();
                    eventPrep[2] = new java.sql.Timestamp(timestamp.getTime());
                    eventPrep[3] = XLogUtils.getAttributeValue(event.getAttributes().get(XOrganizationalExtension.KEY_RESOURCE));
                    eventPrep[4] = XLogUtils.getAttributeValue(event.getAttributes().get(XLifecycleExtension.KEY_MODEL));

                    // add additional attributes
                    int j = 5;
                    for (var attr : eventAttributes) {
                        var value = XLogUtils.getAttributeValue(event.getAttributes().get(attr.getKey()));
                        eventPrep[j] = value;
                        j++;
                    }

                    prepInsertEvent.add(eventPrep);
                }

                // execute buffer?
                buffer++;
                if (buffer >= BUFFER_SIZE) {
                    jdbcTemplate.batchUpdate(insertEventSql, prepInsertEvent);
                    jdbcTemplate.batchUpdate(insertTraceSql, prepInsertTrace);

                    prepInsertEvent.clear();
                    prepInsertTrace.clear();

                    buffer = 0;
                }
            }

            jdbcTemplate.batchUpdate(insertEventSql, prepInsertEvent);
            jdbcTemplate.batchUpdate(insertTraceSql, prepInsertTrace);

            prepInsertEvent.clear();
            prepInsertTrace.clear();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            throw ex;
        }

        // create depending views
        generateCaseView();

        logger.info("Finished importing event log \"{}\"", this.logName);
        return true;
    }

    private void generateCaseView() {
        // generate directly follows graph
        var sql = new OutputBuilder();
        sql.print("CREATE OR REPLACE VIEW " + db.caseTable.getTableNameSQL() + " AS ");
        sql.print("SELECT");
        sql.print("%s,", "case_id");
        sql.print("MIN(timestamp) AS start_time,");
        sql.print("MAX(timestamp) AS end_time,");
        sql.print("COUNT(%s) AS %s,", "event", "num_events");
        sql.print("COUNT(DISTINCT %s) AS %s,", "resource", "num_users");
        sql.print("CAST(%s AS interval) AS %s,", "age(MAX(timestamp), MIN(timestamp))", "total_duration");
        sql.print("CONCAT(':', STRING_AGG(CAST(event AS VARCHAR(5)), '::' ORDER BY timestamp, lifecycle, event), ':') AS variant,");
        sql.print("CONCAT(':', STRING_AGG(CAST(resource AS VARCHAR(255)), '::' ORDER BY timestamp, lifecycle, resource), ':') AS resource_variant,");
        sql.print("HASHTEXT(STRING_AGG(CAST(event AS VARCHAR(5)), '::' ORDER BY timestamp, lifecycle, event)) AS variant_id,");
        sql.print("HASHTEXT(STRING_AGG(CAST(resource AS VARCHAR(255)), '::' ORDER BY timestamp, lifecycle, resource)) AS resource_variant_id");
        sql.print("FROM %s AS log", db.eventTable.getTableNameSQL());
        sql.print("GROUP BY case_id");

        jdbcTemplate.execute(sql.toString());
    }

    /**
     * Drops and create a new events table.
     *
     * @param eventAttributes
     */
    private void generateEventsTable(List<XAttribute> eventAttributes) {
        // drop old table
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + getEventsTableName(this.logName));

        eventAttributes.forEach(x -> {
            if (x instanceof XAttributeContinuous) {
                db.eventTable.addColumn("\"" + x.getKey() + "\"", "integer", null, null);
            } else {
                db.eventTable.addColumn("\"" + x.getKey() + "\"", "varchar", 1024, null);
            }
        });

        // create new table
        String sql = new CreateTableQuery(db.eventTable, true)
                .validate().toString();

        jdbcTemplate.execute(sql);

        // create index
        jdbcTemplate.execute("CREATE INDEX p_case_id_index_" + db.eventTable.getTableNameSQL() + " ON " + db.eventTable.getTableNameSQL() + " (case_id)");
    }

    /**
     * Only return real event attributes that were not already stored using standard xes extensions.
     *
     * @param eventAttributes
     * @return
     */
    private List<XAttribute> getEventAttributes(List<XAttribute> eventAttributes) {
        // remove standard event attributes
        var attributes = new ArrayList<XAttribute>();
        for (var attr : eventAttributes) {
            if (!attr.getKey().equals(XConceptExtension.KEY_NAME) &&
                    !attr.getKey().equals(XOrganizationalExtension.KEY_RESOURCE) &&
                    !attr.getKey().equals(XTimeExtension.KEY_TIMESTAMP) &&
                    !attr.getKey().equals(XLifecycleExtension.KEY_TRANSITION)) {
                attributes.add(attr);
            }
        }

        return attributes;
    }

    /**
     * Drops and generates a new case attribute table.
     *
     * @param attributes
     */
    private void generateCaseAttributeTable(List<XAttribute> attributes) {
        // drop old table
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + getCaseAttributeTableName(this.logName));

        // create new table
        var sql = new StringBuilder();
        sql.append("CREATE TABLE " + getCaseAttributeTableName(this.logName) + " (");
        sql.append("case_id INTEGER,");
        sql.append("original_case_id VARCHAR(1024),");

        for (int i = 0; i < attributes.size(); i++) {
            XAttribute attribute = attributes.get(i);

            if (attribute instanceof XAttributeTimestamp) {
                sql.append("\"" + attribute.getKey() + "\" timestamp" + (i < attributes.size() - 1 ? "," : ""));
            } else {
                sql.append("\"" + attribute.getKey() + "\" VARCHAR(1024)" + (i < attributes.size() - 1 ? "," : ""));
            }
        }

        sql.append(")");

        jdbcTemplate.execute(sql.toString());

        // create index
        jdbcTemplate.execute("CREATE INDEX p_case_id_index_" + db.caseAttributeTable.getTableNameSQL() + " ON " + db.caseAttributeTable.getTableNameSQL() + " (case_id)");
    }

    /**
     * Generates the activities name table which stores the mapping between event id and event name.
     *
     * @param logInfo
     */
    private void generateActivitiesTable(XLogInfo logInfo) {
        // drop old table
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + getActivityTableName(this.logName));

        // create new table
        var sql = new CreateTableQuery(db.activityTable, true)
                .validate().toString();

        jdbcTemplate.execute(sql);

        // add all activities
        List<Object[]> insertValues = new ArrayList<>();
        for (var eventClass : logInfo.getEventClasses(classifier).getClasses()) {
            insertValues.add(new Object[]{eventClass.getIndex(), eventClass.getId()});
        }

        var insertSql = new InsertQuery(db.activityTable)
                .addPreparedColumnCollection(db.activityTable.getColumns())
                .validate().toString();
        jdbcTemplate.batchUpdate(insertSql, insertValues);
    }


}
