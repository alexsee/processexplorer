package org.processmining.plugins.bpmnminer.fitness;

import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.bpmnminer.GroupedXLog;
import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.types.EventLogTaskMapper;

public abstract class AbstractFitness {

    protected final EventLogTaskMapper mapper;
    protected final CausalNet net;

    public AbstractFitness(EventLogTaskMapper m, CausalNet n) {
        mapper = m;
        net = n;
    }

    public abstract void reset();

    public abstract double getFitness();

    public void replayLog(XLog log) {
        reset();
        for (int i = 0; i < log.size(); i++) {
            replayTrace(log.get(i), 1);
        }
    }

    public void replayLog(GroupedXLog gLog) {
        reset();
        for (int i = 0; i < gLog.size(); i++) {
            replayTrace(gLog.get(i).get(0), gLog.get(i).size());
        }
    }

    public void replayTrace(XTrace trace) {
        replayTrace(trace, 1);
    }

    public abstract void replayTrace(XTrace trace, int traceCount);
}
