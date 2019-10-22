package de.tk.processmining.data.query.condition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import de.tk.processmining.data.DatabaseModel;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PathCondition.class, name = "path"),
        @JsonSubTypes.Type(value = AttributeCondition.class, name = "attribute"),
        @JsonSubTypes.Type(value = VariantCondition.class, name = "variant"),
        @JsonSubTypes.Type(value = ClusterCondition.class, name = "cluster"),
        @JsonSubTypes.Type(value = ComboCondition.class, name = "combo"),
        @JsonSubTypes.Type(value = NotCondition.class, name = "not")
})
public abstract class Condition {

    public abstract com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db);

}
