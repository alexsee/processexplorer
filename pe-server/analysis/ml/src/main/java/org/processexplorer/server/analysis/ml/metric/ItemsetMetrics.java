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

package org.processexplorer.server.analysis.ml.metric;

import org.processexplorer.server.analysis.ml.itemsets.Itemset;
import org.processexplorer.server.analysis.query.model.FieldValue;

import java.util.Set;

public class ItemsetMetrics {

    public static double calculateItemsetDistance(Set<FieldValue> i1, Set<FieldValue> i2) {
        Itemset tmp = new Itemset();
        tmp.addAll(i1);
        tmp.retainAll(i2);

        double inBoth = tmp.size();
        return 1 - (2 * inBoth / (i1.size() + i2.size()));
    }

}
