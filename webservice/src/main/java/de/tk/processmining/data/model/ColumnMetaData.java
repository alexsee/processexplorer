package de.tk.processmining.data.model;

public class ColumnMetaData {

    private String columnName;

    private String columnType;

    public ColumnMetaData() {
    }

    public ColumnMetaData(String columnName, String columnType) {
        this.columnName = columnName;
        this.columnType = columnType;
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
}
