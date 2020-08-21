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

import java.util.List;

public class CasesQuery extends BaseQuery {

    private List<String> attributes;

    public CasesQuery() {
    }

    public CasesQuery(String logName, List<String> attributes) {
        super(logName);
        this.attributes = attributes;
    }

    public CasesQuery(String logName, List<Condition> conditions, List<String> attributes) {
        super(logName, conditions);
        this.attributes = attributes;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }
}
