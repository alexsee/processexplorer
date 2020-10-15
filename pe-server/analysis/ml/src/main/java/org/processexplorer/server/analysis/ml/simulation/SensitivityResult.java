package org.processexplorer.server.analysis.ml.simulation;

/**
 * @author Alexander Seeliger on 15.10.2020.
 */
public class SensitivityResult {

    private SensitivityValue start;

    private SensitivityValue end;

    public SensitivityValue getStart() {
        return start;
    }

    public void setStart(SensitivityValue start) {
        this.start = start;
    }

    public SensitivityValue getEnd() {
        return end;
    }

    public void setEnd(SensitivityValue end) {
        this.end = end;
    }
}
