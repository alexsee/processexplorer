package org.processexplorer.server.analysis.query.rowmapper;

import org.processexplorer.server.analysis.query.model.Variant;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alexander Seeliger on 28.08.2020.
 */
public class VariantRowMapper implements RowMapper<Variant> {
    @Override
    public Variant mapRow(ResultSet rs, int rowNum) throws SQLException {
        var result = new Variant();
        result.setId(rs.getLong("variant_id"));
        result.setOccurrence(rs.getLong("occurrence"));
        return result;
    }
}
