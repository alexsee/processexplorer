package de.tk.processmining.data.query.selection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.tk.processmining.data.DatabaseModel;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CaseAttributeSelection.class, name = "case_attribute"),
        @JsonSubTypes.Type(value = CountCasesSelection.class, name = "count_cases"),
        @JsonSubTypes.Type(value = CountVariantsSelection.class, name = "count_variants")
})
public abstract class Selection {

    private SelectionOrder ordering;

    public abstract Object getSelection(DatabaseModel db);

    public abstract String getName();

    public boolean isGroup() {
        return false;
    }

    public SelectionOrder getOrdering() {
        return ordering;
    }

    public void setOrdering(SelectionOrder ordering) {
        this.ordering = ordering;
    }
}
