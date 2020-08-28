package org.processexplorer.server.analysis.query.rowmapper;

import org.processexplorer.server.analysis.query.model.Log;
import org.processexplorer.server.analysis.query.model.Variant;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alexander Seeliger on 28.08.2020.
 */
public class ExtendedVariantRowMapper implements RowMapper<Variant> {

    private final Log logStats;

    public ExtendedVariantRowMapper(Log logStats) {
        this.logStats = logStats;
    }

    @Override
    public Variant mapRow(ResultSet rs, int rowNum) throws SQLException {
        var result = new Variant();
        result.setId(rs.getLong("variant_id"));
        result.setOccurrence(rs.getLong("occurrence"));

        var path = rs.getString("variant").split("::");
        var pathIndex = new int[path.length];

        for (int i = 0; i < path.length; i++) {
            var index = Integer.parseInt(path[i].replace(":", ""));

            path[i] = logStats.getActivities().get(index).getName();
            pathIndex[i] = index;
        }

        result.setPath(path);
        result.setPathIndex(pathIndex);
        return result;
    }
}
