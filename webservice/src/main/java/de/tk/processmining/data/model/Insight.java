package de.tk.processmining.data.model;

import de.tk.processmining.data.analysis.categorization.AnalysisTargetCodes;
import de.tk.processmining.data.analysis.categorization.DomainCodes;
import de.tk.processmining.data.analysis.categorization.EventAttributeCodes;
import de.tk.processmining.data.analysis.categorization.VisualizationCodes;

import java.util.List;
import java.util.Map;

/**
 * @author Alexander Seeliger on 30.09.2019.
 */
public class Insight {

    private String title;

    private String subTitle;

    private double effectSize;

    private double averageWithin;

    private double averageWithout;

    private double stddevWithin;

    private double stddevWithout;

    private InsightValueFormat format;

    private List<String> labels;

    private double[] within;

    private double[] without;

    private List<AnalysisTargetCodes> analysisTargetCodes;

    private List<DomainCodes> domainCodes;

    private List<EventAttributeCodes> eventAttributeCodes;

    private List<VisualizationCodes> visualizationCodes;

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

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public double[] getWithin() {
        return within;
    }

    public void setWithin(double[] within) {
        this.within = within;
    }

    public double[] getWithout() {
        return without;
    }

    public void setWithout(double[] without) {
        this.without = without;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public List<AnalysisTargetCodes> getAnalysisTargetCodes() {
        return analysisTargetCodes;
    }

    public void setAnalysisTargetCodes(List<AnalysisTargetCodes> analysisTargetCodes) {
        this.analysisTargetCodes = analysisTargetCodes;
    }

    public List<DomainCodes> getDomainCodes() {
        return domainCodes;
    }

    public void setDomainCodes(List<DomainCodes> domainCodes) {
        this.domainCodes = domainCodes;
    }

    public List<EventAttributeCodes> getEventAttributeCodes() {
        return eventAttributeCodes;
    }

    public void setEventAttributeCodes(List<EventAttributeCodes> eventAttributeCodes) {
        this.eventAttributeCodes = eventAttributeCodes;
    }

    public List<VisualizationCodes> getVisualizationCodes() {
        return visualizationCodes;
    }

    public void setVisualizationCodes(List<VisualizationCodes> visualizationCodes) {
        this.visualizationCodes = visualizationCodes;
    }
}
