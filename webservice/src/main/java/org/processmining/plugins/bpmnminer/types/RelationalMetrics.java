package org.processmining.plugins.bpmnminer.types;

import org.processmining.plugins.bpmnminer.util.StringUtils;

public class RelationalMetrics {
	protected IntegerEventLog log;
	protected MinerSettings settings;
	
	protected DFTable basicRelations;
	
	public RelationalMetrics(IntegerEventLog l, MinerSettings s) {
		log = l;
		settings = s;
		
		basicRelations = new DFTable(log);
	}
	
	/**
	 * Calculates the (explicit) dependency measure between two tasks
	 * using the Wilson statistical score.
	 * @param taskA Originating task
	 * @param taskB Dependent task
	 * @return Dependency measure
	 * 
	 */
	public double wilsonScore(double positives, double total, double z) {
		if (total == 0)
			return 0D;
		double phat = positives / total;
		double score = (phat 
				+ Math.pow(z, 2) / (2*total) 
				- z * Math.sqrt((phat*(1-phat) + Math.pow(z, 2) / (4*total)) / total)
				)
				/ (1 + Math.pow(z, 2)/total);
		return score;
	}
	
	/**
	 * Calculates the (explicit) dependency measure between two tasks
	 * using HM technique without difference (no distance is taken into account).
	 * @param taskA Originating task
	 * @param taskB Dependent task
	 * @return Dependency measure
	 * 
	 */
	public double getDependencyMeasure(int taskA, int taskB) {
		double ab = basicRelations.getABMeasure(taskA, taskB);
		double ba = basicRelations.getABMeasure(taskB, taskA);
		double measure = (ab) / (ab + ba + settings.dependencyDivisor);
		return measure;
	}
	
	/**
	 * Calculates the (explicit) L1L dependency measure between two equal
	 * tasks using HM technique.
	 * @param taskA Originating and dependent task
	 * @return Dependency measure
	 */
	public double getL1lMeasure(int taskA) {
		double aa = basicRelations.getABMeasure(taskA, taskA);
		double measure = (aa) / (aa + settings.dependencyDivisor);
		return measure;
	}
	
	/**
	 * Calculates the (explicit) L2l dependency measure between two tasks
	 * using HM technique.
	 * @param taskA Originating task
	 * @param taskB Dependent task
	 * @return Dependency measure
	 */
	public double getL2lMeasure(int taskA, int taskB) {
		double ab = basicRelations.getABAMeasure(taskA, taskB);
		double ba = basicRelations.getABAMeasure(taskB, taskA);
		double measure = (ab + ba) / (ab + ba + settings.dependencyDivisor);
		return measure;
	}
	
	/**
	 * Calculates the implicit dependency measure between two tasks
	 * using Flexible HM technique.
	 * @param taskA Originating task
	 * @param taskB Dependent task
	 * @return Dependency measure
	 */
	public double getLdMeasure(int taskA, int taskB) {
		double ab = basicRelations.getAXBMeasure(taskA, taskB);
		double a = basicRelations.getAMeasure(taskA);
		double b = basicRelations.getAMeasure(taskB);
		double measure = ((2.0 * ab) / (a + b + settings.dependencyDivisor))
				- ((2.0 * Math.abs(a - b)) / (a + b + settings.dependencyDivisor));
		return measure;
	}

	public DFTable getBasicRelations() {
		return basicRelations;
	}


	public String toString() {
		int[] index2tasks = log.getTasks();
		String repr 
				= "======= \t ======= \t ======= \t ======= \t ======= \t ======= \n"
				+ " taskA  \t  taskB  \t   dep   \t l1l (A) \t   l2l   \t    LD   \n"
				+ "------- \t ------- \t ------- \t ------- \t ------- \t ------- \n";
		for (int i = 0; i < index2tasks.length; i++) {
			for (int j = 0; j < index2tasks.length; j++) {
				int a = index2tasks[i];
				int b = index2tasks[j];
				repr 	+= StringUtils.padDouble(a, 7)
						+" \t "+ StringUtils.padDouble(b, 7)
						+" \t "+ StringUtils.padDouble(getDependencyMeasure(a, b), 7)
						+" \t "+ StringUtils.padDouble(getL1lMeasure(a), 7)
						+" \t "+ StringUtils.padDouble(getL2lMeasure(a, b), 7)
						+" \t "+ StringUtils.padDouble(getLdMeasure(a, b), 7)
						+"\n";
			}
		}
		repr += "------- \t -------- \t -------- \t -------- \t -------- \t -------- \n";
		
		return repr;
	}
}
