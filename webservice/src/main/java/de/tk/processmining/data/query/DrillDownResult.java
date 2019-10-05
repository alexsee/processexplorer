package de.tk.processmining.data.query;

import de.tk.processmining.data.model.ColumnMetaData;

import java.util.ArrayList;
import java.util.List;

public class DrillDownResult {

    private List<ColumnMetaData> metaData;

    private List<Object> data;

    public DrillDownResult() {
        this.metaData = new ArrayList<>();
        this.data = new ArrayList<>();
    }

    public List<ColumnMetaData> getMetaData() {
        return metaData;
    }

    public void setMetaData(List<ColumnMetaData> metaData) {
        this.metaData = metaData;
    }

    public List<Object> getData() {
        return data;
    }

    public void setData(List<Object> data) {
        this.data = data;
    }
}
