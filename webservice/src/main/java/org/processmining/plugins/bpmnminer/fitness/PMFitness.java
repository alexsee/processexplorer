package org.processmining.plugins.bpmnminer.fitness;

import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.types.EventLogTaskMapper;

public class PMFitness extends ICSFitness {

	public PMFitness(EventLogTaskMapper m, CausalNet n) {
		super(m, n);
	}
	
	public double getFitness() {
		return (double)parsedSequences / (double)playedSequences;
	}
}
