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

package org.processexplorer.server.analysis.query.request;

import org.processexplorer.server.analysis.query.condition.Condition;

import java.util.List;

public class CaseAttributeValueQuery extends BaseQuery {

    private String attributeName;

    public CaseAttributeValueQuery() {
    }

    public CaseAttributeValueQuery(String logName, String attributeName) {
        super(logName);
        this.attributeName = attributeName;
    }

    public CaseAttributeValueQuery(String logName, List<Condition> conditions, String attributeName) {
        super(logName, conditions);
        this.attributeName = attributeName;
    }


    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

}
