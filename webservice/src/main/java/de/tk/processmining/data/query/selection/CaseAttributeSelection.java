package de.tk.processmining.data.query.selection;

import de.tk.processmining.data.DatabaseModel;
import de.tk.processmining.data.analysis.categorization.EventAttributeCodes;
import de.tk.processmining.webservice.database.EventLogAnnotationRepository;

import java.util.List;
import java.util.stream.Collectors;

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

    @Override
    public List<EventAttributeCodes> getCodes(EventLogAnnotationRepository repository, String logName) {
        return repository
                .findByLogNameAndColumnTypeAndColumnName(logName, "case_attribute", attributeName)
                .stream()
                .map(x -> EventAttributeCodes.valueOf(x.getCode()))
                .collect(Collectors.toList());
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }
}
