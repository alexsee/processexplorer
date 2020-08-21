/*
 * ProcessExplorer
 * Copyright (C) 2008-2013 Philippe Fournier-Viger
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

package org.processexplorer.server.analysis.ml.itemsets.spmf.sequentialpatterns;

import java.util.ArrayList;
import java.util.List;

class Pair {
    // the item
    protected final int item;

    // List of the pseudosequences of the projection with this item .
    private List<PseudoSequence> pseudoSequences = new ArrayList<PseudoSequence>();

    /**
     * Constructor
     *
     * @param postfix indicate if this is the case of an item appearing in an itemset that is cut at the left because of
     *                a projection
     * @param item    the item
     */
    Pair(Integer item) {
        this.item = item;
    }

    /**
     * Check if two pairs are equal (same item and both appears in a postfix or not).
     *
     * @return true if equals.
     */
    public boolean equals(Object object) {
        Pair pair = (Pair) object;
        return pair.item == this.item;
    }

    /**
     * Method to calculate an hashcode (because pairs are stored in a map).
     */
    public int hashCode() {// Ex: 127333,P,X,1  127333,N,Z,2
        // transform it into a string
        // then use the hashcode method from the string class
        return item + "".hashCode();
    }


    /**
     * Get the item represented by this pair
     *
     * @return the item.
     */
    public int getItem() {
        return item;
    }

    /**
     * Get the support of this item (the number of sequences containing it).
     *
     * @return the support (an integer)
     */
    public int getCount() {
        return pseudoSequences.size();
    }

    /**
     * Get the list of sequence IDs associated with this item.
     *
     * @return the list of sequence IDs.
     */
    public List<PseudoSequence> getPseudoSequences() {
        return pseudoSequences;
    }
}