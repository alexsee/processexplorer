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

package org.processexplorer.server.analysis.ml.metric;

import org.apache.commons.math3.stat.StatUtils;

/**
 * @author Alexander Seeliger on 01.10.2019.
 */
public class StatisticMetrics {

    /**
     * Calculates the Cohens' d effect size for two given sets of double values.
     *
     * @param x
     * @param y
     * @return
     */
    public static double effectByCohensD(double[] x, double[] y) {
        double meanX = StatUtils.mean(x);
        double meanY = StatUtils.mean(y);

        return (meanX - meanY) / Math.sqrt((cohenVariance(x, meanX) + cohenVariance(y, meanY)) / 2);
    }

    /**
     * Calculates the variance for a double array.
     *
     * @param x
     * @param mean
     * @return
     */
    public static double cohenVariance(double[] x, double mean) {
        double result = 0;

        for (double v : x) {
            result += Math.pow(v - mean, 2);
        }

        return (1 / (double) (x.length - 1)) * result;
    }

    /**
     * Norms the given values by the sum.
     *
     * @param values
     * @return
     */
    public static double[] norm(double[] values) {
        double count = StatUtils.sum(values);
        double[] p = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            p[i] = values[i] / count;
        }
        return p;
    }
}
