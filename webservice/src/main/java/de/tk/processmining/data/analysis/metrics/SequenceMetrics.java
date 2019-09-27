package de.tk.processmining.data.analysis.metrics;

import java.util.Arrays;
import java.util.List;

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

}
