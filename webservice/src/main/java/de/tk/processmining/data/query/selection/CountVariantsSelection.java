package de.tk.processmining.data.query.selection;

import com.healthmarketscience.sqlbuilder.FunctionCall;
import de.tk.processmining.data.DatabaseModel;

public class CountVariantsSelection extends Selection {

    @Override
    public Object getSelection(DatabaseModel db) {
        return FunctionCall.count().setIsDistinct(true).addColumnParams(db.variantsIdCol);
    }

    @Override
    public String getName() {
        return "\"Variant count\"";
    }

    @Override
    public boolean isGroup() {
        return true;
    }

}
