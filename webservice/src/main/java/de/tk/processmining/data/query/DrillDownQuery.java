package de.tk.processmining.data.query;

import de.tk.processmining.data.query.selection.Selection;

import java.util.List;

public class DrillDownQuery extends BaseQuery {

    private List<Selection> selections;

    public DrillDownQuery() {
    }

    public DrillDownQuery(String logName, List<Selection> selections) {
        super(logName);
        this.selections = selections;
    }

    public List<Selection> getSelections() {
        return selections;
    }

    public void setSelections(List<Selection> selections) {
        this.selections = selections;
    }

}
