/*
 * ProcessExplorer
 * Copyright (C) 2019  Alexander Seeliger
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.processmining.data.query.selection;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.processmining.data.DatabaseModel;
import de.processmining.data.analysis.categorization.EventAttributeCodes;
import de.processmining.webservice.database.EventLogAnnotationRepository;

import java.util.List;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CaseAttributeSelection.class, name = "case_attribute"),
        @JsonSubTypes.Type(value = CountCasesSelection.class, name = "count_cases"),
        @JsonSubTypes.Type(value = CountVariantsSelection.class, name = "count_variants")
})
public abstract class Selection {

    private SelectionOrder ordering;

    private String alias;

    public abstract Object getSelection(DatabaseModel db);

    public abstract String getName();

    public abstract List<EventAttributeCodes> getCodes(EventLogAnnotationRepository repository, String logName);

    public boolean isGroup() {
        return false;
    }

    public SelectionOrder getOrdering() {
        return ordering;
    }

    public void setOrdering(SelectionOrder ordering) {
        this.ordering = ordering;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }
}
