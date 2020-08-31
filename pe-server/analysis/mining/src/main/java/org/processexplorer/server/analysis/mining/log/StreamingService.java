package org.processexplorer.server.analysis.mining.log;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.CustomExpression;
import com.healthmarketscience.sqlbuilder.InsertQuery;
import com.healthmarketscience.sqlbuilder.UpdateQuery;
import org.processexplorer.server.analysis.query.DatabaseModel;
import org.processexplorer.server.analysis.query.model.Event;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Alexander Seeliger on 25.08.2020.
 */
@Service
public class StreamingService {

    private final JdbcTemplate jdbcTemplate;

    public StreamingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Adds a new event to an existing case.
     *
     * @param logName
     * @param caseId
     * @param event
     */
    public void addEvent(String logName, String caseId, Event event) {
        var db = new DatabaseModel(logName);

//        // update entry
//        var updateSQL = new UpdateQuery(db.eventTable)
//                .addSetClause(db.eventTargetEventCol, event.getActivity().getId())
//                .addSetClause(db.eventTargetTimestampCol, event.getTimestamp())
//                .addSetClause(db.eventTargetResourceCol, event.getResource())
//                .addCondition(new BinaryCondition(BinaryCondition.Op.EQUAL_TO, db.eventCaseIdCol, caseId))
//                .addCondition(new BinaryCondition(BinaryCondition.Op.EQUAL_TO, db.eventTargetEventCol, -2));
//
//        jdbcTemplate.update(updateSQL.validate().toString());
//
//        // append end symbol
//        var insertSQL = new InsertQuery(db.eventTable)
//                .addColumn(db.eventCaseIdCol, caseId)
//                .addColumn(db.eventEventCol, event.getActivity().getId())
//                .addColumn(db.eventTargetEventCol, -2)
//                .addColumn(db.eventTimestampCol, event.getTimestamp())
//                .addColumn(db.eventTargetTimestampCol, null)
//                .addColumn(db.eventResourceCol, event.getResource())
//                .addColumn(db.eventTargetResourceCol, null)
//                .addColumn(db.eventDurationCol, null)
//                .addColumn(db.eventNumberCol, null);
//
//        jdbcTemplate.update(insertSQL.validate().toString());
//
//        // update case statistics
//        var updateStatsSQL = new UpdateQuery(db.eventTable)
//                .addSetClause(db.eventDurationCol, new CustomExpression("age(" + db.eventEventCol.getColumnNameSQL() + ", " + db.eventTargetEventCol.getColumnNameSQL() + ")"))
//                .addCondition(new BinaryCondition(BinaryCondition.Op.EQUAL_TO, db.eventCaseIdCol, caseId));
//
//        jdbcTemplate.update(updateStatsSQL.validate().toString());
    }
}