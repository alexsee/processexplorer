package de.tk.processmining.data.model;

/**
 * @author Alexander Seeliger on 30.09.2019.
 */
public class Insight {

    private String insight;

    private double effectSize;

    private double averageWithin;

    private double averageWithout;

    private double stddevWithin;

    private double stddevWithout;

    private InsightValueFormat format;

    public String getInsight() {
        return insight;
    }

    public void setInsight(String insight) {
        this.insight = insight;
    }

    public double getEffectSize() {
        return effectSize;
    }

    public void setEffectSize(double effectSize) {
        this.effectSize = effectSize;
    }

    public double getAverageWithin() {
        return averageWithin;
    }

    public void setAverageWithin(double averageWithin) {
        this.averageWithin = averageWithin;
    }

    public double getAverageWithout() {
        return averageWithout;
    }

    public void setAverageWithout(double averageWithout) {
        this.averageWithout = averageWithout;
    }

    public double getStddevWithin() {
        return stddevWithin;
    }

    public void setStddevWithin(double stddevWithin) {
        this.stddevWithin = stddevWithin;
    }

    public double getStddevWithout() {
        return stddevWithout;
    }

    public void setStddevWithout(double stddevWithout) {
        this.stddevWithout = stddevWithout;
    }

    public InsightValueFormat getFormat() {
        return format;
    }

    public void setFormat(InsightValueFormat format) {
        this.format = format;
    }
}
