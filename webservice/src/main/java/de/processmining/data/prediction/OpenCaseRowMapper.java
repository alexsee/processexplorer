/*
 * ProcessExplorer
 * Copyright (C) 2020  Alexander Seeliger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.processmining.data.prediction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Alexander Seeliger on 19.08.2020.
 */
public class OpenCaseRowMapper implements RowMapper<OpenCaseResult> {

    @Override
    public OpenCaseResult mapRow(ResultSet resultSet, int i) throws SQLException {
        var result = new OpenCaseResult();
        result.setCaseId(resultSet.getLong("case_id"));
        result.setTimestampStart(resultSet.getTimestamp("start_time"));
        result.setTimestampEnd(resultSet.getTimestamp("end_time"));
        result.setNumEvents(resultSet.getInt("num_events"));
        result.setNumResources(resultSet.getInt("num_users"));
        result.setState(resultSet.getInt("state"));
        result.setAssignedTo(resultSet.getString("assigned"));
        result.setCurrentActivity(resultSet.getString("current_event"));
        result.setCurrentResource(resultSet.getString("current_resource"));

        try {
            var objectMapper = new ObjectMapper();
            var predictions = objectMapper.readTree(resultSet.getString("prediction"));
            var values = predictions.elements();

            while (values.hasNext()) {
                var prediction = values.next();

                var name = prediction.findValue("name").asText();
                if (name.equals("name")) {
                    result.setNextActivity(prediction.findValues("prediction").get(0).findValue("value").asText());
                } else if (name.equals("resource")) {
                    result.setNextResource(prediction.findValues("prediction").get(0).findValue("value").asText());
                }
            }
        } catch (Exception ex) {

        }

        return result;
    }
}
