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

package org.processexplorer.server.analysis.query.condition;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.processexplorer.server.analysis.query.DatabaseModel;

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
        @JsonSubTypes.Type(value = NotCondition.class, name = "not"),
        @JsonSubTypes.Type(value = ReworkCondition.class, name = "rework"),
        @JsonSubTypes.Type(value = DurationCondition.class, name = "duration")
})
public abstract class Condition {

    public abstract com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db);

}
