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

package org.processexplorer.server.analysis.ml.clustering.model;

import org.processexplorer.server.analysis.query.model.FieldValue;
import org.processexplorer.server.analysis.query.model.Variant;

import java.util.HashSet;
import java.util.Set;

public class VariantCluster {

    private Set<Variant> variants;

    private Set<FieldValue> itemsets;

    private Set<Set<FieldValue>> isets;

    private double silhouette = 0;

    public VariantCluster(Set<FieldValue> itemsets, Set<Variant> variants) {
        this.itemsets = new HashSet<>(itemsets);
        this.variants = variants;
        this.isets = new HashSet<>();
    }

    public Set<Variant> getVariants() {
        return variants;
    }

    public void setVariants(Set<Variant> variants) {
        this.variants = variants;
    }

    public Set<FieldValue> getItemsets() {
        return itemsets;
    }

    public void setItemsets(Set<FieldValue> itemsets) {
        this.itemsets = itemsets;
    }

    public Set<Set<FieldValue>> getIsets() {
        return isets;
    }

    public void setIsets(Set<Set<FieldValue>> isets) {
        this.isets = isets;
    }

    public double getSilhouette() {
        return silhouette;
    }

    public void setSilhouette(double silhouette) {
        this.silhouette = silhouette;
    }

    @Override
    public int hashCode() {
        return variants.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VariantCluster) {
            VariantCluster other = (VariantCluster) obj;
            return other.variants.equals(variants);
        }
        return false;
    }
}
