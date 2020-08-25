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

package org.processexplorer.server.analysis.query.selection;

import com.healthmarketscience.sqlbuilder.FunctionCall;
import org.processexplorer.server.analysis.query.DatabaseModel;
import org.processexplorer.server.analysis.query.codes.EventAttributeCodes;
import org.processexplorer.server.common.persistence.repository.EventLogAnnotationRepository;

import java.util.List;

public class CountVariantsSelection extends Selection {

    @Override
    public Object getSelection(DatabaseModel db) {
        return FunctionCall.count().setIsDistinct(true).addColumnParams(db.caseVariantIdCol);
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
