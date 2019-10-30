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

package de.processmining.data;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
class DatabaseConstants {

    private static final String ACTIVITIES_TABLE = "_activity_names";

    private static final String EVENTS_TABLE = "_events";

    private static final String CASE_ATTRIBUTES_TABLE = "_case_attributes";

    private static final String CASE_TABLE = "_cases";

    private static final String VARIANTS_TABLE = "_variants";

    private static final String GRAPH_TABLE = "_graph";

    static String getActivityTableName(String logName) {
        return logName.toLowerCase() + DatabaseConstants.ACTIVITIES_TABLE;
    }

    static String getEventsTableName(String logName) {
        return logName.toLowerCase() + DatabaseConstants.EVENTS_TABLE;
    }

    static String getCaseAttributeTableName(String logName) {
        return logName.toLowerCase() + DatabaseConstants.CASE_ATTRIBUTES_TABLE;
    }

    static String getGraphTableName(String logName) {
        return logName.toLowerCase() + DatabaseConstants.GRAPH_TABLE;
    }

    static String getCaseTableName(String logName) {
        return logName.toLowerCase() + CASE_TABLE;
    }

    static String getVariantsTableName(String logName) {
        return logName.toLowerCase() + VARIANTS_TABLE;
    }

}
