package de.tk.processmining.data.query.selection;

import com.healthmarketscience.sqlbuilder.FunctionCall;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.analysis.categorization.EventAttributeCodes;
import de.tk.processmining.webservice.database.EventLogAnnotationRepository;

import java.util.List;

public class CountVariantsSelection extends Selection {

    @Override
    public Object getSelection(DatabaseModel db) {
        return FunctionCall.count().setIsDistinct(true).addColumnParams(db.variantsIdCol);
    }

    @Override
    public String getName() {
        return "\"Count variants\"";
    }

    @Override
    public List<EventAttributeCodes> getCodes(EventLogAnnotationRepository repository, String logName) {
        return null;
    }

    @Override
    public boolean isGroup() {
        return true;
    }

}
