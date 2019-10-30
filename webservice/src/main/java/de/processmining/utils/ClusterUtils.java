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

package de.processmining.utils;

import smile.clustering.HierarchicalClustering;

import java.util.Arrays;

/**
 * @author Alexander Seeliger on 27.09.2019.
 */
public class ClusterUtils {

    /**
     * Calculates the optimal number of clusters using the elbow method.
     *
     * @param algorithm
     * @param distanceMatrix
     * @return
     */
    public static int[] calculateOptimalClusters(HierarchicalClustering algorithm, double[][] distanceMatrix) {
        // perform clustering
        int max = Math.min(distanceMatrix.length, 100);

        double[] w = new double[max];

        for (int i = 1; i < max; i++) {
            int[] newResult = new int[distanceMatrix.length];
            if (i == 1) {
                Arrays.fill(newResult, 0);
            } else {
                newResult = algorithm.partition(i);
            }
            w[i - 1] = w(distanceMatrix, newResult);

            if (w[i - 1] == 0)
                break;
        }

        int bestK = getBestK(w);
        if (bestK == 1) {
            return new int[]{0};
        } else if (bestK > max) {
            return algorithm.partition(max);
        } else {
            return algorithm.partition(bestK);
        }
    }

    public static double w(double[][] distanceMatrix, int[] clusters) {
        double wk = 0;
        int[] clusterIndexes = Arrays.stream(clusters).distinct().toArray();

        for (int cluster : clusterIndexes) {
            // calculate intra-cluster distance
            double dist = 0, count = 0;

            for (int i = 0; i < clusters.length; i++) {
                if (clusters[i] != cluster)
                    continue;

                for (int j = i; j < clusters.length; j++) {
                    if (clusters[j] != cluster)
                        continue;
                    dist += distanceMatrix[i][j];
                    count++;
                }
            }

            dist = dist / count;
            wk += dist;
        }

        return wk;
    }

    public static int getBestK(double[] w) {
        double[] slopes = new double[w.length - 1];
        for (int i = 0; i < w.length - 1; i++) {
            slopes[i] = w[i] - w[i + 1];
        }

        double[] angles = new double[slopes.length - 1];

        double maxVal = 0;
        int max = -1;

        for (int i = 0; i < slopes.length - 1; i++) {
            angles[i] = slopes[i] - slopes[i + 1];

            if (angles[i] > maxVal) {
                max = i;
                maxVal = angles[i];
            }
        }

        return max + 1;
    }
}
