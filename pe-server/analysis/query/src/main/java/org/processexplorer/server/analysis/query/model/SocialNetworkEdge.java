/*
 * ProcessExplorer
 * Copyright (C) 2020  Alexander Seeliger
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

package org.processexplorer.server.analysis.query.model;

/**
 * @author Alexander Seeliger on 17.08.2020.
 */
public class SocialNetworkEdge {

    private String sourceResource;
    private String targetResource;

    private long avgDuration;
    private long minDuration;
    private long maxDuration;

    private long occurrence;

    private int[] variants;

    public String getSourceResource() {
        return sourceResource;
    }

    public void setSourceResource(String sourceResource) {
        this.sourceResource = sourceResource;
    }

    public String getTargetResource() {
        return targetResource;
    }

    public void setTargetResource(String targetResource) {
        this.targetResource = targetResource;
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
