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

package de.processmining.data.analysis.metrics;

import de.processmining.data.model.Variant;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
public class SequenceMetrics {

    /**
     * Calculates the Levenshtein distance for two sequences.
     *
     * @param a
     * @param b
     * @return
     */
    public static double getLevenshteinDistance(int[] a, int[] b) {
        if (Arrays.equals(a, b)) {
            return 0;
        }

        int[] swap = a;

        // Swapping the strings if `a` is longer than `b` so we know which one is the
        // shortest & which one is the longest
        if (a.length > b.length) {
            a = b;
            b = swap;
        }

        int aLen = a.length;
        int bLen = b.length;

        // Performing suffix trimming:
        // We can linearly drop suffix common to both strings since they
        // don't increase distance at all
        // Note: `~-` is the bitwise way to perform a `- 1` operation
        while (aLen > 0 && (a[aLen - 1] == b[bLen - 1])) {
            aLen--;
            bLen--;
        }

        // Performing prefix trimming
        // We can linearly drop prefix common to both strings since they
        // don't increase distance at all
        int start = 0;

        while (start < aLen && (a[start] == b[start])) {
            start++;
        }

        aLen -= start;
        bLen -= start;

        if (aLen == 0) {
            return (double) bLen / (double) Math.max(a.length, b.length);
        }

        int[] arr = new int[aLen];
        int[] charCodeCache = new int[aLen];

        int bCharCode;
        int ret = 0;
        int tmp;
        int tmp2;
        int i = 0;
        int j = 0;

        while (i < aLen) {
            charCodeCache[i] = a[start + i];
            arr[i] = ++i;
        }

        while (j < bLen) {
            bCharCode = b[start + j];
            tmp = j++;
            ret = j;

            for (i = 0; i < aLen; i++) {
                tmp2 = bCharCode == charCodeCache[i] ? tmp : tmp + 1;
                tmp = arr[i];
                ret = arr[i] = tmp > ret ? tmp2 > ret ? ret + 1 : tmp2 : tmp2 > tmp ? tmp + 1 : tmp2;
            }
        }

        return (double) ret / (double) Math.max(a.length, b.length);
    }

    /**
     * Calculates the Levenshtein distance for two sequences.
     *
     * @return
     */
    public static double getLevenshteinDistance(Variant v1, Variant v2) {
        var a = v1.getPathIndex();
        var b = v2.getPathIndex();

        return getLevenshteinDistance(a, b);
    }

    /**
     * Calculates the difference of the given variants and returns a value that indicates the pairwise distance divided
     * by the number of variants * 2.
     *
     * @param variants1
     * @param variants2
     * @return
     */
    public static double calculateVariantDistance(Collection<Variant> variants1, Collection<Variant> variants2) {
        double dist = 0;
        for (var v1 : variants1) {
            for (var v2 : variants2) {
                if (v1.equals(v2)) {
                    dist += 0;
                } else {
                    dist += getLevenshteinDistance(v1, v2);
                }
            }
        }

        dist /= (variants1.size() * variants2.size());
        return dist;
    }

    /**
     * Calculate the distance of a variant to all variants in the given cluster.
     *
     * @param cluster
     * @param variant
     * @return
     */
    public static double calculateVariantDistanceToCluster(Collection<Variant> cluster, Variant variant) {
        double dist = cluster.stream().mapToDouble(x -> getLevenshteinDistance(x, variant)).sum();
        dist /= cluster.size();

        return dist;
    }

}
