package org.processexplorer.server.analysis.query.rowmapper;

import org.processexplorer.server.analysis.query.model.GraphEdge;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * @author Alexander Seeliger on 28.08.2020.
 */
public class GraphEdgeRowMapper implements RowMapper<GraphEdge> {
    @Override
    public GraphEdge mapRow(ResultSet rs, int rowNum) throws SQLException {
        var result = new GraphEdge();
        result.setSourceEvent(rs.getLong(1));
        result.setTargetEvent(rs.getLong(2));
        result.setAvgDuration(rs.getLong(3));
        result.setMinDuration(rs.getLong(4));
        result.setMaxDuration(rs.getLong(5));
        result.setOccurrence(rs.getLong(6));
        result.setVariants(Arrays.stream(rs.getString(7).split(",")).mapToInt(Integer::parseInt).toArray());

        return result;
    }
}
