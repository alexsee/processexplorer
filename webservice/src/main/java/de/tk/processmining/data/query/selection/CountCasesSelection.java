package de.tk.processmining.data.query.selection;

import com.healthmarketscience.sqlbuilder.FunctionCall;
import de.tk.processmining.data.DatabaseModel;

public class CountCasesSelection extends Selection {

    @Override
    public Object getSelection(DatabaseModel db) {
        return FunctionCall.countAll();
    }

    @Override
    public String getName() {
        return "\"Count cases\"";
    }

    @Override
    public boolean isGroup() {
        return true;
    }
}
