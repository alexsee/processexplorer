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

package org.processexplorer.server.analysis.query.model;

import org.processexplorer.server.analysis.query.codes.EventAttributeCodes;

import java.util.List;

public class ColumnMetaData {

    private String alias;

    private String columnName;

    private String columnType;

    private List<EventAttributeCodes> codes;

    public ColumnMetaData() {
    }

    public ColumnMetaData(String columnName, String columnType, String alias) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.alias = alias;
    }

    public ColumnMetaData(String columnName, String columnType, String alias, List<EventAttributeCodes> codes) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.alias = alias;
        this.codes = codes;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<EventAttributeCodes> getCodes() {
        return codes;
    }

    public void setCodes(List<EventAttributeCodes> codes) {
        this.codes = codes;
    }
}
