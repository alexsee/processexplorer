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

package de.processmining.data.analysis.clustering.model;

import java.util.List;

public class EventLogClusters {

	private double minSupport = 0;

	private List<VariantCluster> clusters;

	private double silhouetteCoefficient = 0;

	private double weighting = 0;
	
	public EventLogClusters() {
		
	}
	
	public EventLogClusters(List<VariantCluster> clusters) {
		this.clusters = clusters;
	}
	
	public List<VariantCluster> getClusters() {
		return this.clusters;
	}

	public double getMinSupport() {
		return minSupport;
	}

	public void setMinSupport(double minSupport) {
		this.minSupport = minSupport;
	}

	public double getSilhouetteCoefficient() {
		return silhouetteCoefficient;
	}

	public void setSilhouetteCoefficient(double silhouetteCoefficient) {
		this.silhouetteCoefficient = silhouetteCoefficient;
	}

	public double getWeighting() {
		return weighting;
	}

	public void setWeighting(double weighting) {
		this.weighting = weighting;
	}
}
