package de.tk.processmining.data;

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
