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
import de.processmining.webservice.ApplicationContextProvider;
import org.apache.commons.lang3.StringUtils;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeContinuous;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
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

        var dataSource = ApplicationContextProvider.getApplicationContext().getBean(DataSource.class);

        try {
            var prepInsertEvent = dataSource.getConnection().prepareStatement(insertEventSql);
            var prepInsertTrace = dataSource.getConnection().prepareStatement(insertTraceSql);

            // import events and cases
            int buffer = 0;
            for (int i = 0; i < log.size(); i++) {
                var trace = log.get(i);

                var caseId = XLogUtils.getAttributeValue(trace.getAttributes().get(XConceptExtension.KEY_NAME)).toString();

                // obtain trace attributes
                prepInsertTrace.setInt(1, i);
                prepInsertTrace.setString(2, caseId);

                for (int j = 0; j < traceAttributes.size(); j++) {
                    var value = XLogUtils.getAttributeValue(trace.getAttributes().get(traceAttributes.get(j).getKey()));

                    if (value instanceof Date) {
                        prepInsertTrace.setObject(j + 3, new java.sql.Timestamp(((Date) value).getTime()));
                    } else {
                        prepInsertTrace.setObject(j + 3, value);
                    }
                }
                prepInsertTrace.addBatch();

                // obtain events for trace
                for (var event : trace) {
                    var timestamp = (Date) XLogUtils.getAttributeValue(event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP));

                    prepInsertEvent.setInt(1, i);
                    prepInsertEvent.setString(2, caseId);
                    prepInsertEvent.setInt(3, logInfo.getEventClasses().getClassOf(event).getIndex());
                    prepInsertEvent.setString(4, classifier.getClassIdentity(event));
                    prepInsertEvent.setObject(5, XLogUtils.getAttributeValue(event.getAttributes().get(XOrganizationalExtension.KEY_RESOURCE)));
                    prepInsertEvent.setTimestamp(6, new java.sql.Timestamp(timestamp.getTime()));
                    prepInsertEvent.setObject(7, XLogUtils.getAttributeValue(event.getAttributes().get(XLifecycleExtension.KEY_MODEL)));

                    // add additional attributes
                    int j = 8;
                    for (var attr : eventAttributes) {
                        var value = XLogUtils.getAttributeValue(event.getAttributes().get(attr.getKey()));
                        prepInsertEvent.setObject(j, value);

                        j++;
                    }

                    prepInsertEvent.addBatch();
                }

                // execute buffer?
                buffer++;
                if (buffer >= bufferSize) {
                    prepInsertEvent.executeBatch();
                    prepInsertTrace.executeBatch();

                    buffer = 0;
                }
            }

            prepInsertEvent.executeBatch();
            prepInsertTrace.executeBatch();

            prepInsertEvent.close();
            prepInsertTrace.close();
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return false;
        }

        logger.info("Finished importing event log \"{}\"", this.logName);
        return true;
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
                db.eventTable.addColumn("\"" + x.getKey() + "\"", "varchar", 255, null);
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
        sql.append("original_case_id VARCHAR(250),");

        for (int i = 0; i < attributes.size(); i++) {
            XAttribute attribute = attributes.get(i);

            if (attribute instanceof XAttributeTimestamp) {
                sql.append("\"" + attribute.getKey() + "\" timestamp" + (i < attributes.size() - 1 ? "," : ""));
            } else {
                sql.append("\"" + attribute.getKey() + "\" VARCHAR(250)" + (i < attributes.size() - 1 ? "," : ""));
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
