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

package de.processmining.data.analysis.itemsets.spmf.frequentpatterns;

import ca.pfv.spmf.patterns.itemset_array_integers_with_count.Itemset;

import java.util.ArrayList;
import java.util.List;

/**
 * This is an implementation of a MFI-Node as used by the FPMax algorithm.
 *
 * @see MFITree
 * @see Itemset
 * @see AlgoFPMax
 * @author Philippe Fournier-Viger, 2015
 */
public class MFINode {
	int itemID = -1;  // item id
//	int counter = 1;  // frequency counter  (a.k.a. support)
	int level;  // at which level in the MFI tree this node appears
	
	// the parent node of that node or null if it is the root
	MFINode parent = null; 
	// the child nodes of that node
	List<MFINode> childs = new ArrayList<MFINode>();
	
	MFINode nodeLink = null; // link to next node with the same item id (for the header table).
	
	/**
	 * constructor
	 */
	MFINode(){
		
	}

	/**
	 * Return the immediate child of this node having a given ID.
	 * If there is no such child, return null;
	 */
	MFINode getChildWithID(int id) {
		// for each child node
		for(MFINode child : childs){
			// if the id is the one that we are looking for
			if(child.itemID == id){
				// return that node
				return child;
			}
		}
		// if not found, return null
		return null;
	}
	
	/**
	 * Method for getting a string representation of this tree 
	 * (to be used for debugging purposes).
	 * @param an indentation
	 * @return a string
	 */
	public String toString(String indent) {
		StringBuilder output = new StringBuilder();
		output.append(""+ itemID);
//		output.append(" (count="+ counter);
		output.append(" level="+ level);
		output.append(")\n");
		String newIndent = indent + "   ";
		for (MFINode child : childs) {
			output.append(newIndent+ child.toString(newIndent));
		}
		return output.toString();
	}

}
