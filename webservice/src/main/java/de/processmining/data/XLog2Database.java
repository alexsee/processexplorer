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

import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import de.processmining.utils.OutputBuilder;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static de.processmining.data.DatabaseConstants.*;

/**
 * @author Alexander Seeliger on 20.09.2019.
 */
public class XLog2Database {

    private static Logger logger = LoggerFactory.getLogger(XLog2Database.class);

    private final int bufferSize = 1000;

    private String logName;

    private XEventClassifier classifier = new XEventNameClassifier();

    private JdbcTemplate jdbcTemplate;

    private DatabaseModel db;

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
                XEvent previousEvent = null;
                int e = 0;

                for (var event : trace) {
                    var timestamp = (Date) XLogUtils.getAttributeValue(event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP));
                    var timestampPrevious = previousEvent == null ? null : (Date) XLogUtils.getAttributeValue(previousEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP));

                    var eventPrep = new Object[11 + eventAttributes.size()];

                    eventPrep[0] = i;
                    eventPrep[1] = caseId;
                    eventPrep[2] = previousEvent == null ? -1 : logInfo.getEventClasses().getClassOf(previousEvent).getIndex();
                    eventPrep[3] = logInfo.getEventClasses().getClassOf(event).getIndex();
                    eventPrep[4] = previousEvent == null ? null : new java.sql.Timestamp(timestampPrevious.getTime());
                    eventPrep[5] = new java.sql.Timestamp(timestamp.getTime());
                    eventPrep[6] = previousEvent == null ? null : XLogUtils.getAttributeValue(previousEvent.getAttributes().get(XOrganizationalExtension.KEY_RESOURCE));
                    eventPrep[7] = XLogUtils.getAttributeValue(event.getAttributes().get(XOrganizationalExtension.KEY_RESOURCE));
                    eventPrep[8] = null;
                    eventPrep[9] = e;
                    eventPrep[10] = XLogUtils.getAttributeValue(event.getAttributes().get(XLifecycleExtension.KEY_MODEL));

                    // add additional attributes
                    int j = 11;
                    for (var attr : eventAttributes) {
                        var value = XLogUtils.getAttributeValue(event.getAttributes().get(attr.getKey()));
                        eventPrep[j] = value;
                        j++;
                    }

                    prepInsertEvent.add(eventPrep);
                    previousEvent = event;
                    e++;
                }

                // insert end event
                prepInsertEvent.add(new Object[]{
                        i,
                        caseId,
                        logInfo.getEventClasses().getClassOf(previousEvent).getIndex(),
                        -2,
                        new java.sql.Timestamp(((Date) XLogUtils.getAttributeValue(previousEvent.getAttributes().get(XTimeExtension.KEY_TIMESTAMP))).getTime()),
                        null,
                        XLogUtils.getAttributeValue(previousEvent.getAttributes().get(XOrganizationalExtension.KEY_RESOURCE)),
                        null,
                        null,
                        e,
                        null
                });

                // execute buffer?
                buffer++;
                if (buffer >= bufferSize) {
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

        // clean tables
        jdbcTemplate.update("DROP TABLE IF EXISTS " + db.caseTable.getTableNameSQL());
        jdbcTemplate.update("DROP TABLE IF EXISTS " + db.variantsTable.getTableNameSQL());
        jdbcTemplate.execute(getPathsTableQuery(db.variantsTable.getTableNameSQL()));

        // generate directly follows graph
        var sql = new OutputBuilder();
        sql.print("WITH");
        sql.print("sel AS (");
        sql.print("  SELECT");
        sql.print("    %s,", "case_id");
        sql.print("    MIN(source_timestamp) AS start_time,");
        sql.print("    MAX(source_timestamp) AS end_time,");
        sql.print("    COUNT(%s) AS %s,", "source_event", "num_events");
        sql.print("    COUNT(DISTINCT %s) AS %s,", "source_resource", "num_users");
        sql.print("    CAST(%s AS interval) AS %s,", "age(MAX(source_timestamp), MIN(source_timestamp))", "total_duration");
        sql.print("    CONCAT(':', STRING_AGG(CAST(source_event AS VARCHAR(5)), '::' ORDER BY source_timestamp, lifecycle, source_event), ':') AS variant");
        sql.print("  FROM %s AS log", db.eventTable.getTableNameSQL());
        sql.print("  WHERE source_event <> -1");
        sql.print("  GROUP BY case_id),");

        sql.print("  ins AS (");
        sql.print("    INSERT INTO %s (%s)", db.variantsTable.getTableNameSQL(), "variant");
        sql.print("    SELECT %s FROM sel GROUP BY variant ORDER BY COUNT(*) DESC RETURNING %s AS %s, %s)", "variant", "id", "variant_id", "variant");
        sql.print("SELECT %s, %s, %s, %s, %s, %s, %s",
                "sel.case_id",
                "sel.start_time",
                "sel.end_time",
                "sel.num_events",
                "sel.num_users",
                "sel.total_duration",
                "variant_id");
        sql.print("INTO %s", db.caseTable.getTableNameSQL());
        sql.print("FROM sel LEFT JOIN ins USING (variant)");

        jdbcTemplate.execute(sql.toString());

        // create index for cases table
        jdbcTemplate.execute("CREATE INDEX p_case_id_index_" + db.caseTable.getTableNameSQL() + " ON " + db.caseTable.getTableNameSQL() + " (case_id)");
        jdbcTemplate.execute("UPDATE " + db.eventTable.getTableNameSQL() + " SET " + db.eventDurationCol.getColumnNameSQL() + " = age(" + db.eventTargetTimestampCol.getColumnNameSQL() + "," + db.eventSourceTimestampCol.getColumnNameSQL() + ") WHERE " + db.eventDurationCol.getColumnNameSQL() + " IS NULL");

        logger.info("Finished importing event log \"{}\"", this.logName);
        return true;
    }

    private String getPathsTableQuery(String variantsTableName) {
        OutputBuilder sql = new OutputBuilder();
        sql.print("CREATE TABLE %s (", variantsTableName);
        sql.indentPrint("%s %s,", "id", "SERIAL NOT NULL PRIMARY KEY");
        sql.indentPrint("%s %s NULL", "variant", "TEXT");
        sql.print(")");

        return sql.toString();
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
