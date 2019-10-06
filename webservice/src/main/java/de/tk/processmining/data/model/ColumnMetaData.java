package de.tk.processmining.data.model;

public class ColumnMetaData {

    private String alias;

    private String columnName;

    private String columnType;

    public ColumnMetaData() {
    }

    public ColumnMetaData(String columnName, String columnType, String alias) {
        this.columnName = columnName;
        this.columnType = columnType;
        this.alias = alias;
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
}
