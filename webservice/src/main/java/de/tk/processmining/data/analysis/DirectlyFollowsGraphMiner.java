package de.tk.processmining.data.analysis;

import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.utils.OutputBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
public class DirectlyFollowsGraphMiner {

    private static Logger logger = LoggerFactory.getLogger(DirectlyFollowsGraphMiner.class);

    private JdbcTemplate jdbcTemplate;

    public DirectlyFollowsGraphMiner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void mine(String logName) {
        logger.info("Begin generating directly-follows graph for \"{}\"", logName);

        var db = new DatabaseModel(logName);

        // clean tables
        jdbcTemplate.update("DROP TABLE IF EXISTS " + db.caseTable.getTableNameSQL());
        jdbcTemplate.update("DROP TABLE IF EXISTS " + db.variantsTable.getTableNameSQL());
        jdbcTemplate.execute(getPathsTableQuery(db.variantsTable.getTableNameSQL()));

        jdbcTemplate.update("DROP TABLE IF EXISTS " + logName + "_graph");

        // generate directly follows graph
        var sql = new OutputBuilder();
        sql.print("WITH");
        sql.print("sel AS (");
        sql.print("  SELECT");
        sql.print("    %s,", "case_id");
        sql.print("    MIN(timestamp) AS start_time,");
        sql.print("    MAX(timestamp) AS end_time,");
        sql.print("    COUNT(%s) AS %s,", "event_name", "num_events");
        sql.print("    COUNT(DISTINCT %s) AS %s,", "resource", "num_users");
        sql.print("    CAST(%s AS interval) AS %s,", "age(MAX(timestamp), MIN(timestamp))", "duration");
        sql.print("    CONCAT(':', STRING_AGG(CAST(event_id AS VARCHAR(5)), '::' ORDER BY timestamp, lifecycle, event_id), ':') AS variant");
        sql.print("  FROM %s AS log", db.eventTable.getTableNameSQL());
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
                "sel.duration",
                "variant_id");
        sql.print("INTO %s", db.caseTable.getTableNameSQL());
        sql.print("FROM sel LEFT JOIN ins USING (variant)");

        jdbcTemplate.execute(sql.toString());

        // create index for cases table
        jdbcTemplate.execute("CREATE INDEX p_case_id_index_" + db.caseTable.getTableNameSQL() + " ON " + db.caseTable.getTableNameSQL() + " (case_id)");

        // generate graph?
        sql.clear();
        sql.print("WITH ordered AS");
        sql.print("  (SELECT");
        sql.print("    ROW_NUMBER() OVER (PARTITION BY case_id ORDER BY timestamp, lifecycle, event_id) AS num_rows,");
        sql.print("    *");
        sql.print("  FROM %s)", db.eventTable.getTableNameSQL());
        sql.print("SELECT");
        sql.print("  DENSE_RANK() OVER (ORDER BY COALESCE(a.event_name, 'Startknoten'), COALESCE(b.event_name, 'Endknoten')) AS edge_id,");
        sql.print("  COALESCE(a.case_id, b.case_id) AS case_id,");
        sql.print("  COALESCE(a.event_name, 'Startknoten') AS source_event,");
        sql.print("  COALESCE(b.event_name, 'Endknoten') AS target_event,");
        sql.print("  COALESCE(%s, interval '0') AS duration,", "age(b.timestamp, a.timestamp)");
        sql.print("  CAST(%s AS INT) AS variant_id", db.caseTable.getTableNameSQL() + ".variant_id");
        sql.print("INTO %s", db.graphTable.getTableNameSQL());
        sql.print("FROM ordered AS a");
        sql.print("INNER JOIN %s ON", db.activityTable.getTableNameSQL());
        sql.print("  %s = %s", db.activityTable.getTableNameSQL() + ".id", "a.event_id");
        sql.print("FULL OUTER JOIN ordered AS b ON");
        sql.print("  b.case_id = a.case_id");
        sql.print("  AND b.num_rows = a.num_rows + 1");
        sql.print("LEFT JOIN " + db.caseTable.getTableNameSQL() + " ON COALESCE(a.case_id, b.case_id) = " + db.caseTable.getTableNameSQL() + ".case_id");

        jdbcTemplate.execute(sql.toString());

        // create index
        jdbcTemplate.execute("CREATE INDEX p_case_id_index_" + db.graphTable.getTableNameSQL() + " ON " + logName + "_graph (case_id)");

        logger.info("Finished generating directly-follows graph for \"{}\"", logName);
    }

    private String getPathsTableQuery(String variantsTableName) {
        OutputBuilder sql = new OutputBuilder();
        sql.print("CREATE TABLE %s (", variantsTableName);
        sql.indentPrint("%s %s,", "id", "SERIAL NOT NULL PRIMARY KEY");
        sql.indentPrint("%s %s NULL", "variant", "TEXT");
        sql.print(")");

        return sql.toString();
    }

}
