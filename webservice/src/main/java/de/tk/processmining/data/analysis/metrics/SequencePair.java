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

package de.tk.processmining.data.analysis.metrics;

import java.util.List;
import java.util.Objects;

/**
 * @author Alexander Seeliger on 23.04.2018.
 */
public class SequencePair {

    private List<Integer> s1;

    private List<Integer> s2;

    public SequencePair(List<Integer> s1, List<Integer> s2) {
        this.s1 = s1;
        this.s2 = s2;
    }

    public List<Integer> getS1() {
        return s1;
    }

    public List<Integer> getS2() {
        return s2;
    }

    @Override
    public int hashCode() {
        return Objects.hash(s1, s2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SequencePair) {
            SequencePair other = (SequencePair) obj;

            return other.s1.equals(this.s1) && other.s2.equals(this.s2) || other.s1.equals(this.s2) && other.s2.equals(this.s1);
        } else {
            return false;
        }
    }
}
