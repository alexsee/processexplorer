/*
 * ProcessExplorer
 * Copyright (C) 2019  Alexander Seeliger
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

package de.processmining.data.query.condition;

import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.InCondition;
import de.processmining.data.DatabaseModel;

/**
 * @author Alexander Seeliger on 13.12.2019.
 */
public class DurationCondition extends Condition {

    private String from;

    private String to;

    private String unit;

    private Long minDuration;

    private Long maxDuration;

    public DurationCondition() {

    }

    private long getDuration(long duration) {
        if (unit.equals("days")) {
            return duration * 60 * 60 * 24;
        } else if (unit.equals("hours")) {
            return duration * 60 * 60;
        } else {
            return duration * 60;
        }
    }

    @Override
    public com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db) {
        var activityFrom = from == null ? "Startknoten" : from;
        var activityTo = to == null ? "Endknoten" : to;

        var innerSQL = "(select a.case_id from " + db.graphTable.getTableNameSQL() + " as a, " + db.graphTable.getTableNameSQL() + " as b " +
                "where a.case_id = b.case_id " +
                "and a.source_event = '" + activityFrom + "' " +
                "and b.target_event = '" + activityTo + "' ";
        innerSQL += "group by a.case_id ";
        innerSQL += "having ";

        if (minDuration != null) {
            innerSQL += "age(max(b.target_timestamp), min(a.source_timestamp)) >= interval '" + getDuration(minDuration) + "' ";
        }
        if (maxDuration != null) {
            innerSQL += ((minDuration != null) ? "and " : "") + "age(max(b.target_timestamp), min(a.source_timestamp)) <= interval '" + getDuration(maxDuration) + "' ";
        }

        innerSQL += ")";

        return new InCondition(db.caseAttributeCaseIdCol, new CustomSql(innerSQL));
    }

    public String getTo() {
        return to;
    }

    public Long getMaxDuration() {
        return maxDuration;
    }

    public Long getMinDuration() {
        return minDuration;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setMaxDuration(Long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void setMinDuration(Long minDuration) {
        this.minDuration = minDuration;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
