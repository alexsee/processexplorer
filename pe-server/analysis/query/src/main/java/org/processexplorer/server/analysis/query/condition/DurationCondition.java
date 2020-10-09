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

package org.processexplorer.server.analysis.query.condition;

import com.healthmarketscience.sqlbuilder.CustomSql;
import com.healthmarketscience.sqlbuilder.InCondition;
import org.processexplorer.server.analysis.query.DatabaseModel;

/**
 * @author Alexander Seeliger on 13.12.2019.
 */
public class DurationCondition extends Condition {

    private Integer from;

    private Integer to;

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
        var activityFrom = from == null ? -1 : from;
        var activityTo = to == null ? -2 : to;

        var innerSQL = "(select a.case_id from " + db.eventTable.getTableNameSQL() + " as a, " + db.eventTable.getTableNameSQL() + " as b " +
                "where a.case_id = b.case_id " +
                (activityFrom == -1 ? "" : "and a.event = " + activityFrom + " ") +
                (activityTo == -2 ? "": "and b.event = " + activityTo + " ");
        innerSQL += "group by a.case_id ";
        innerSQL += "having ";

        if (minDuration != null) {
            innerSQL += "age(max(b.timestamp), min(a.timestamp)) >= interval '" + getDuration(minDuration) + "' ";
        }
        if (maxDuration != null) {
            innerSQL += ((minDuration != null) ? "and " : "") + "age(max(b.timestamp), min(a.timestamp)) <= interval '" + getDuration(maxDuration) + "' ";
        }

        innerSQL += ")";

        return new InCondition(db.caseAttributeCaseIdCol, new CustomSql(innerSQL));
    }

    public Integer getTo() {
        return to;
    }

    public Long getMaxDuration() {
        return maxDuration;
    }

    public Long getMinDuration() {
        return minDuration;
    }

    public Integer getFrom() {
        return from;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public void setMaxDuration(Long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public void setMinDuration(Long minDuration) {
        this.minDuration = minDuration;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
