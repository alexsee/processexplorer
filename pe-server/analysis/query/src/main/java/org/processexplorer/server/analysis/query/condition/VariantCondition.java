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

import com.healthmarketscience.sqlbuilder.BinaryCondition;
import com.healthmarketscience.sqlbuilder.InCondition;
import org.processexplorer.server.analysis.query.DatabaseModel;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
public class VariantCondition extends Condition {

    private Long[] variantIds;

    public VariantCondition() {
    }

    public VariantCondition(Long[] variantIds) {
        this.variantIds = variantIds;
    }

    @Override
    public com.healthmarketscience.sqlbuilder.Condition getCondition(DatabaseModel db) {
        if (variantIds != null) {
            return new InCondition(db.caseVariantIdCol, variantIds);
        }
        return null;
    }

    public Long[] getVariantIds() {
        return variantIds;
    }

    public void setVariantIds(Long[] variantIds) {
        this.variantIds = variantIds;
    }
}
