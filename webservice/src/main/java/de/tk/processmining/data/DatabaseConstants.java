package de.tk.processmining.data;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
public class DatabaseConstants {

    public static final String ACTIVITIES_TABLE = "_activity_names";

    public static final String EVENTS_TABLE = "_events";

    public static final String CASE_ATTRIBUTES_TABLE = "_case_attributes";

    public static final String CASE_TABLE = "_cases";

    private static final String VARIANTS_TABLE = "_variants";

    public static String getActivityTableName(String logName) {
        return logName.toLowerCase() + DatabaseConstants.ACTIVITIES_TABLE;
    }

    public static String getEventsTableName(String logName) {
        return logName.toLowerCase() + DatabaseConstants.EVENTS_TABLE;
    }

    public static String getCaseAttributeTableName(String logName) {
        return logName.toLowerCase() + DatabaseConstants.CASE_ATTRIBUTES_TABLE;
    }

    public static String getCaseTableName(String logName) {
        return logName.toLowerCase() + CASE_TABLE;
    }

    public static String getVariantsTableName(String logName) {
        return logName.toLowerCase() + VARIANTS_TABLE;
    }

//    public static String identifier(String fieldName) {
//        return "\"" + fieldName + "\"";
//    }
//
//    public static String identifier(String alias, String fieldName) {
//        return "\"" + alias + "\".\"" + fieldName + "\"";
//    }
}
