package de.tk.processmining.data.analysis.metrics;

import static org.apache.commons.math3.stat.StatUtils.*;

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
        double meanX = mean(x);
        double meanY = mean(y);

        return (meanX - meanY) / Math.sqrt((variance(x) + variance(y)) / 2);
    }

    public static double[] norm(double[] values) {
        double count = sum(values);
        double[] p = new double[values.length];
        for(int i = 0; i < values.length; i++) {
            p[i] = values[i] / count;
        }
        return p;
    }
}
