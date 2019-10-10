package de.tk.processmining.data.query.selection;

import com.healthmarketscience.sqlbuilder.FunctionCall;
import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.analysis.categorization.EventAttributeCodes;
import de.tk.processmining.webservice.database.EventLogAnnotationRepository;

import java.util.List;

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
    public List<EventAttributeCodes> getCodes(EventLogAnnotationRepository repository, String logName) {
        return null;
    }

    @Override
    public boolean isGroup() {
        return true;
    }
}
