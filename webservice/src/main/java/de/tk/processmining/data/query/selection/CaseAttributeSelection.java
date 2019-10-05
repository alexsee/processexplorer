package de.tk.processmining.data.query.selection;

import de.tk.processmining.data.DatabaseModel;

public class CaseAttributeSelection extends Selection {

    private String attributeName;

    @Override
    public Object getSelection(DatabaseModel db) {
        return db.caseAttributeTable.addColumn(getName());
    }

    @Override
    public String getName() {
        return "\"" + this.attributeName + "\"";
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
