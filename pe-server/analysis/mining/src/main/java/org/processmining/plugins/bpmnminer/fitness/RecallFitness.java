package org.processmining.plugins.bpmnminer.fitness;

import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.types.EventLogTaskMapper;

public class RecallFitness extends ICSFitness {

	public RecallFitness(EventLogTaskMapper m, CausalNet n) {
		super(m, n);
	}
	
	public double getFitness() {
		return (double)parsedTasks / ((double)parsedTasks + (double)unparsedTasks);
	}
}
