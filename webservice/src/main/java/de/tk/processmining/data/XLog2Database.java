package de.tk.processmining.data;

import com.healthmarketscience.sqlbuilder.CreateTableQuery;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import org.apache.commons.lang3.StringUtils;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.classification.XEventNameClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static de.tk.processmining.data.DatabaseConstants.*;

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

    public void importLog(XLog log) {
        logger.info("Begin importing event log \"{}\"", this.logName);

        // read general log properties
        var logInfo = XLogInfoFactory.createLogInfo(log);
        var traceAttributes = log.getGlobalTraceAttributes();
        var eventAttributes = log.getGlobalEventAttributes();

        // generate tables
        generateActivitiesTable(logInfo);
        generateCaseAttributeTable(traceAttributes);
        generateEventsTable();

        String insertEventSql = new InsertQuery(db.eventTable)
                .addPreparedColumnCollection(db.eventTable.getColumns())
                .validate().toString();
        String insertTraceSql = "INSERT INTO " + getCaseAttributeTableName(this.logName) + " VALUES (" + StringUtils.repeat("?", ",", traceAttributes.size() + 2) + ");";

        // import events and cases
        var insertEventValues = new ArrayList<Object[]>();
        var insertTraceValues = new ArrayList<Object[]>();

        int buffer = 0;
        for (int i = 0; i < log.size(); i++) {
            var trace = log.get(i);

            var caseId = XLogUtils.getAttributeValue(trace.getAttributes().get(XConceptExtension.KEY_NAME)).toString();

            // obtain trace attributes
            var traceValues = new Object[traceAttributes.size() + 2];
            traceValues[0] = i;
            traceValues[1] = caseId;

            for (int j = 0; j < traceAttributes.size(); j++) {
                traceValues[j + 2] = XLogUtils.getAttributeValue(trace.getAttributes().get(traceAttributes.get(j).getKey()));
            }

            insertTraceValues.add(traceValues);

            // obtain events for trace
            for (var event : trace) {
                Object[] values = new Object[]{
                        i,
                        caseId,
                        logInfo.getEventClasses().getClassOf(event).getIndex(),
                        classifier.getClassIdentity(event),
                        XLogUtils.getAttributeValue(event.getAttributes().get("org:resource")),
                        XLogUtils.getAttributeValue(event.getAttributes().get(XTimeExtension.KEY_TIMESTAMP)),
                        XLogUtils.getAttributeValue(event.getAttributes().get(XLifecycleExtension.KEY_MODEL))
                };

                insertEventValues.add(values);
            }

            // execute buffer?
            buffer++;
            if (buffer >= bufferSize) {
                jdbcTemplate.batchUpdate(insertEventSql, insertEventValues);
                jdbcTemplate.batchUpdate(insertTraceSql, insertTraceValues);

                buffer = 0;
                insertEventValues.clear();
                insertTraceValues.clear();
            }
        }

        jdbcTemplate.batchUpdate(insertEventSql, insertEventValues);
        jdbcTemplate.batchUpdate(insertTraceSql, insertTraceValues);

        logger.info("Finished importing event log \"{}\"", this.logName);
    }

    /**
     * Drops and create a new events table.
     */
    private void generateEventsTable() {
        // drop old table
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + getEventsTableName(this.logName));

        // create new table
        String sql = new CreateTableQuery(db.eventTable, true)
                .validate().toString();

        jdbcTemplate.execute(sql);
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

            sql.append("\"" + attribute.getKey() + "\" VARCHAR(250)" + (i < attributes.size() - 1 ? "," : ""));
        }

        sql.append(")");

        jdbcTemplate.execute(sql.toString());
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
