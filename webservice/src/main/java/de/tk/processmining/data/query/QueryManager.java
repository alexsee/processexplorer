package de.tk.processmining.data.query;

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.FunctionCall;
import com.healthmarketscience.sqlbuilder.OrderObject;
import com.healthmarketscience.sqlbuilder.SelectQuery;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbColumn;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSchema;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbSpec;
import com.healthmarketscience.sqlbuilder.dbspec.basic.DbTable;
import de.tk.processmining.data.model.Log;
import de.tk.processmining.data.model.Variant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static de.tk.processmining.data.DatabaseConstants.*;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
@Service
public class QueryManager {

    private JdbcTemplate jdbcTemplate;

    private DbSpec spec = new DbSpec();
    private DbSchema schema = spec.addDefaultSchema();

    DbTable variantsTable;
    DbColumn variantsIdCol;
    DbColumn variantsVariantCol;

    DbTable caseTable;
    DbColumn caseVariantIdCol;

    @Autowired
    public QueryManager(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void init(String logName) {
        variantsTable = schema.addTable(getVariantsTableName(logName));
        variantsIdCol = variantsTable.addColumn("id");
        variantsVariantCol = variantsTable.addColumn("variant");

        caseTable = schema.addTable(getCaseTableName(logName));
        caseVariantIdCol = caseTable.addColumn("variant_id");
    }

    /**
     * Returns basic statistics about a loaded event log.
     *
     * @param logName
     * @return
     */
    public Log getLogStatistics(String logName) {
        var activities = jdbcTemplate.queryForList("SELECT name FROM " + getActivityTableName(logName) + " ORDER BY id", String.class);
        var numEvents = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + getEventsTableName(logName), Long.class);
        var numTraces = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM " + getCaseAttributeTableName(logName), Long.class);

        var result = new Log();
        result.setLogName(logName);
        result.setNumActivities(activities.size());
        result.setActivities(activities);
        result.setNumEvents(numEvents);
        result.setNumTraces(numTraces);

        return result;
    }

    /**
     * Returns all existing paths of a loaded event log with corresponding occurrence and variant id.
     *
     * @param logName
     * @return
     */
    public List<Variant> getAllPaths(String logName) {
        var logStats = getLogStatistics(logName);

        init(logName);

        var sql = new SelectQuery()
                .addAllTableColumns(variantsTable)
                .addAliasedColumn(FunctionCall.count().addColumnParams(caseVariantIdCol), "occurrence")
                .addJoin(SelectQuery.JoinType.INNER, variantsTable, caseTable, BinaryCondition.equalTo(variantsIdCol, caseVariantIdCol))
                .addGroupings(variantsIdCol)
                .addCustomOrdering("occurrence", OrderObject.Dir.DESCENDING)
                .validate().toString();

        var rowMapper = new RowMapper<Variant>() {
            public Variant mapRow(ResultSet rs, int rowNum) throws SQLException {
                var result = new Variant();
                result.setId(rs.getLong(1));
                result.setOccurrence(rs.getLong(3));

                var path = rs.getString(2).split("::");
                for (int i = 0; i < path.length; i++) {
                    path[i] = logStats.getActivities().get(Integer.parseInt(path[i].replace(":", "")));
                }

                result.setPath(path);
                return result;
            }
        };

        var paths = jdbcTemplate.query(sql, rowMapper);
        return paths;
    }
}
