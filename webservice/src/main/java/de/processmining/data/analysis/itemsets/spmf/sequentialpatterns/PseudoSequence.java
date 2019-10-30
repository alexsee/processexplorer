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

package de.processmining.data.analysis.itemsets.spmf.sequentialpatterns;

public class PseudoSequence {

	// the corresponding sequence in the original database
	protected int sequenceID;

	// the first itemset of this pseudo-sequence in the original sequence
	protected int indexFirstItem;
	
	/**
	 * Get the original sequence corresponding to this projected sequence.
	 * @return the original sequence
	 */
	public int getOriginalSequenceID() {
		return sequenceID;
	}
	
	/**
	 * Create a pseudo-sequence from a sequence that is an original sequence.
	 * @param sequence the original sequence.
	 * @param indexFirstItem the item where the pseudo-sequence should start in terms of the original sequence.
	 */
	protected  PseudoSequence(int sequenceID, int indexFirstItem){
		// remember the original sequence
		this.sequenceID = sequenceID;
		// remember the starting position of this pseudo-sequence in terms
		// of the original sequence.
		this.indexFirstItem = indexFirstItem;
	}
}