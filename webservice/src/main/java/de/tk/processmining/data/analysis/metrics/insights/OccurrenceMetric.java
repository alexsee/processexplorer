package de.tk.processmining.data.analysis.metrics.insights;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.Condition;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.query.QueryManager;
import org.springframework.jdbc.core.JdbcTemplate;

public class OccurrenceMetric implements InsightMetric {

    private String logName;

    private QueryManager queryManager;

    private JdbcTemplate jdbcTemplate;

    public void getInsights(String logName) {
        var log = queryManager.getLogStatistics(logName);
        var activities = log.getActivities();

        var startActivity = activities.get(0);
        var endActivity = activities.get(1);



    }

    private void computeDifference(Condition condition) {
        var db = new DatabaseModel(logName);

        var sql = new SelectQuery()
                .addColumns(db.graphSourceEventCol, db.graphTargetEventCol)
                .addAliasedColumn(FunctionCall.countAll(), "occurrence")
                .addCondition(condition)
                .addJoins(SelectQuery.JoinType.INNER, db.graphVariantJoin)
                .addGroupings(db.graphCaseIdCol, db.graphSourceEventCol, db.graphTargetEventCol)
                .validate().toString();

        var result = jdbcTemplate.queryForList(sql);


    }

}
