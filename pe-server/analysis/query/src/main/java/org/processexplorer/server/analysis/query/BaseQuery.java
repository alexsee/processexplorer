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

package org.processexplorer.server.analysis.query;

import org.processexplorer.server.analysis.query.condition.Condition;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseQuery {

    private String logName;

    private List<Condition> conditions;

    public BaseQuery() {
        this.conditions = new ArrayList<>();
    }

    public BaseQuery(String logName) {
        this.logName = logName;
        this.conditions = new ArrayList<>();
    }

    public BaseQuery(String logName, List<Condition> conditions) {
        this.logName = logName;
        this.conditions = conditions;
    }

    public String getLogName() {
        return logName;
    }

    public void setLogName(String logName) {
        this.logName = logName;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
