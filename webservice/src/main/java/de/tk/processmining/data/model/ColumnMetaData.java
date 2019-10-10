package de.tk.processmining.data.model;

import de.tk.processmining.data.analysis.categorization.EventAttributeCodes;
import de.tk.processmining.webservice.database.entities.EventLogAnnotation;

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
