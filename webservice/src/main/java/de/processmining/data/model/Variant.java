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

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * @author Alexander Seeliger on 23.09.2019.
 */
public class Variant {

    private long id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String[] path;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int[] pathIndex;

    private long occurrence;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String[] getPath() {
        return path;
    }

    public void setPath(String[] path) {
        this.path = path;
    }

    public long getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(long occurrence) {
        this.occurrence = occurrence;
    }

    public int[] getPathIndex() {
        return pathIndex;
    }

    public void setPathIndex(int[] pathIndex) {
        this.pathIndex = pathIndex;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Variant) {
            var other = (Variant) obj;
            return other.getId() == getId();
        }
        return false;
    }
}
