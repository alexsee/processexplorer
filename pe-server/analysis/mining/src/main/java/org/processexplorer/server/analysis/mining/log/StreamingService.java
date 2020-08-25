package org.processexplorer.server.analysis.mining.log;

import com.healthmarketscience.sqlbuilder.*;
import org.hibernate.loader.custom.sql.SQLCustomQuery;
import org.hibernate.sql.Update;
import org.processexplorer.server.analysis.query.DatabaseModel;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * @author Alexander Seeliger on 25.08.2020.
 */
@Service
public class StreamingService {

    private final JdbcTemplate jdbcTemplate;

    public StreamingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void addEvent(String logName, String caseId, long activityId, Timestamp timestamp, String resource) {
        var db = new DatabaseModel(logName);

        // update entry
        var updateSQL = new UpdateQuery(db.eventTable)
                .addSetClause(db.eventTargetEventCol, activityId)
                .addSetClause(db.eventTargetTimestampCol, timestamp)
                .addSetClause(db.eventTargetResourceCol, resource)
                .addCondition(new BinaryCondition(BinaryCondition.Op.EQUAL_TO, db.eventCaseIdCol, caseId))
                .addCondition(new BinaryCondition(BinaryCondition.Op.EQUAL_TO, db.eventTargetEventCol, -2));

        jdbcTemplate.update(updateSQL.validate().toString());

        // append end symbol
        var insertSQL = new InsertQuery(db.eventTable)
                .addColumn(db.eventCaseIdCol, caseId)
                .addColumn(db.eventSourceEventCol, activityId)
                .addColumn(db.eventTargetEventCol, -2)
                .addColumn(db.eventSourceTimestampCol, timestamp)
                .addColumn(db.eventTargetTimestampCol, null)
                .addColumn(db.eventSourceResourceCol, resource)
                .addColumn(db.eventTargetResourceCol, null)
                .addColumn(db.eventDurationCol, null)
                .addColumn(db.eventNumberCol, null);

        jdbcTemplate.update(insertSQL.validate().toString());

        // update case statistics
        var updateStatsSQL = new UpdateQuery(db.eventTable)
                .addSetClause(db.eventDurationCol, new CustomExpression("age(" + db.eventSourceEventCol.getColumnNameSQL() + ", " + db.eventTargetEventCol.getColumnNameSQL() + ")"))
                .addCondition(new BinaryCondition(BinaryCondition.Op.EQUAL_TO, db.eventCaseIdCol, caseId));

        jdbcTemplate.update(updateStatsSQL.validate().toString());
    }

}
