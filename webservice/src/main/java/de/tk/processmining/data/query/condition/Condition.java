package de.tk.processmining.data.query.condition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.tk.processmining.data.DatabaseModel;

import java.util.List;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PathCondition.class, name = "path"),
        @JsonSubTypes.Type(value = AttributeCondition.class, name = "attribute")
})
public abstract class Condition {

    public abstract List<com.healthmarketscience.sqlbuilder.Condition> getCondition(DatabaseModel db);

}
