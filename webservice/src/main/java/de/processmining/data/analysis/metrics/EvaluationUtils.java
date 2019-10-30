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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * @author Alexander Seeliger on 12.02.2018.
 */
public class EvaluationUtils {

    /**
     * Calculates the silhouette coefficient for the given clustering result.
     *
     * @param distancematrix
     * @param labels
     * @return
     */
    public static double silhouetteCoefficient(double[][] distancematrix, int[] labels) {
        List<Double> silresult = new ArrayList<>();

        HashMap<Integer, Integer> classlabels = new HashMap<>();
        int samplenum = labels.length;

        // Get the size of each cluster
        for (int i = 0; i < samplenum; i++) {
            Integer currentlabel = labels[i];
            if (classlabels.containsKey(currentlabel)) {
                int count = classlabels.get(currentlabel) + 1;
                classlabels.put(currentlabel, count);
            } else {
                classlabels.put(currentlabel, 1);
            }
        }

        // OK, now calculate the silhouete
        for (int i = 0; i < samplenum; i++) {
            double silhouettevalue = 0;
            double a = 0;
            double b = 0;
            Integer classlabel = labels[i];

            //initializing
            HashMap<Integer, Double> bvalues = new HashMap<>();

            //calculate distance by different classes
            for (int j = 0; j < samplenum; j++) {
                if (i == j) continue;
                Integer currentclasslabel = labels[j];

                double distancevalue = 0.0;
                if (bvalues.containsKey(currentclasslabel))
                    distancevalue = bvalues.get(currentclasslabel);
                distancevalue = distancevalue + distancematrix[i][j];

                bvalues.put(currentclasslabel, distancevalue);
            }

            //calculate a b and silhouette
            double mindis = Double.MAX_VALUE;
            for (Integer kLabel : bvalues.keySet()) {
                int count = classlabels.get(kLabel);
                double value = bvalues.get(kLabel);
                if (kLabel.equals(classlabel))
                    a = value / count;
                else if (value / count < mindis) {
                    mindis = value / count;
                }
            }
            b = mindis;

            if (a > b) {
                silhouettevalue = (b - a) / a;
            } else {
                silhouettevalue = (b - a) / b;
            }

            silresult.add(silhouettevalue);
        }

        return silresult.stream().mapToDouble(x -> x).sum() / silresult.size();
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
