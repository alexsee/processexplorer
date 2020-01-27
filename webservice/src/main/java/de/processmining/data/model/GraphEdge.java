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

package de.processmining.data.model;

/**
 * @author Alexander Seeliger on 24.09.2019.
 */
public class GraphEdge {

    private long sourceEvent;
    private long targetEvent;

    private long avgDuration;
    private long minDuration;
    private long maxDuration;

    private long occurrence;

    private int[] variants;

    public long getSourceEvent() {
        return sourceEvent;
    }

    public void setSourceEvent(long sourceEvent) {
        this.sourceEvent = sourceEvent;
    }

    public long getTargetEvent() {
        return targetEvent;
    }

    public void setTargetEvent(long targetEvent) {
        this.targetEvent = targetEvent;
    }

    public long getAvgDuration() {
        return avgDuration;
    }

    public void setAvgDuration(long avgDuration) {
        this.avgDuration = avgDuration;
    }

    public long getMinDuration() {
        return minDuration;
    }

    public void setMinDuration(long minDuration) {
        this.minDuration = minDuration;
    }

    public long getMaxDuration() {
        return maxDuration;
    }

    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    public long getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(long occurrence) {
        this.occurrence = occurrence;
    }

    public int[] getVariants() {
        return variants;
    }

    public void setVariants(int[] variants) {
        this.variants = variants;
    }
}
