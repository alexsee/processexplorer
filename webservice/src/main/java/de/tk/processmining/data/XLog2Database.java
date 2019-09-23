package de.tk.processmining.data;

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
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.List;

import static de.tk.processmining.data.DatabaseConstants.*;

/**
 * @author Alexander Seeliger on 20.09.2019.
 */
public class XLog2Database {

    private final int bufferSize = 250;

    private String logName;

    private XEventClassifier classifier = new XEventNameClassifier();

    private JdbcTemplate jdbcTemplate;

    public XLog2Database(JdbcTemplate jdbcTemplate, String logName) {
        this.logName = logName;
        this.jdbcTemplate = jdbcTemplate;
    }

    public void importLog(XLog log) {
        // read general log properties
        var logInfo = XLogInfoFactory.createLogInfo(log);
        var traceAttributes = log.getGlobalTraceAttributes();
        var eventAttributes = log.getGlobalEventAttributes();

        // generate tables
        generateActivitiesTable(logInfo);
        generateCaseAttributeTable(traceAttributes);
        generateEventsTable();

        String insertEventSql = "INSERT INTO " + getEventsTableName(this.logName) + " VALUES (?, ?, ?, ?, ?, ?, ?);";
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
            if (buffer > bufferSize) {
                jdbcTemplate.batchUpdate(insertEventSql, insertEventValues);
                jdbcTemplate.batchUpdate(insertTraceSql, insertTraceValues);

                buffer = 0;
                insertEventValues.clear();
                insertTraceValues.clear();
            }
        }

        jdbcTemplate.batchUpdate(insertEventSql, insertEventValues);
        jdbcTemplate.batchUpdate(insertTraceSql, insertTraceValues);
    }

    /**
     * Drops and create a new events table.
     */
    private void generateEventsTable() {
        // drop old table
        jdbcTemplate.execute("DROP TABLE IF EXISTS " + getEventsTableName(this.logName));

        // create new table
        String sql = "CREATE TABLE " + getEventsTableName(this.logName) + " (" +
                "case_id INTEGER," +
                "original_case_id VARCHAR(250)," +
                "event_id INTEGER," +
                "event_name VARCHAR(250)," +
                "user_name VARCHAR(250)," +
                "timestamp TIMESTAMP," +
                "lifecycle VARCHAR(250)" +
                ")";
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
        var sql = new StringBuilder();
        sql.append("CREATE TABLE " + getActivityTableName(this.logName) + " (");
        sql.append("id INTEGER,");
        sql.append("name VARCHAR(250)");
        sql.append(")");

        jdbcTemplate.execute(sql.toString());

        // add all activities
        List<Object[]> insertValues = new ArrayList<>();
        for (var eventClass : logInfo.getEventClasses(classifier).getClasses()) {
            insertValues.add(new Object[]{eventClass.getIndex(), eventClass.getId()});
        }

        jdbcTemplate.batchUpdate("INSERT INTO " + getActivityTableName(this.logName) + " VALUES (?, ?)", insertValues);
    }


}
