package org.processexplorer.server.analysis.ml.simulation;

/**
 * @author Alexander Seeliger on 14.10.2020.
 */
public class SensitivityValue {

    private double distance;

    private String variation;

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    @Override
    public String toString() {
        return "SensitivityValue{" +
                "distance=" + distance +
                ", variation='" + variation + '\'' +
                '}';
    }
}
