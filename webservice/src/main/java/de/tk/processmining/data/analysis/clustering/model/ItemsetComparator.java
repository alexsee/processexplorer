/*
 * Hybrid Feature Set Clustering
 * Copyright (C) 2018  Alexander Seeliger
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

package de.tk.processmining.data.analysis.clustering.model;

import java.util.Comparator;
import java.util.Map;

public class ItemsetComparator implements Comparator<Itemset> {
	
	private Map<Itemset, Double> support;
	
	public ItemsetComparator(Map<Itemset, Double> support) {
		this.support = support;
	}

	@Override
	public int compare(Itemset o1, Itemset o2) {
		return Double.compare(support.get(o1), support.get(o2));
	}

}
