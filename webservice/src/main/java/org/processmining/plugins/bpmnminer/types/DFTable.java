package org.processmining.plugins.bpmnminer.types;

import org.processmining.plugins.bpmnminer.util.MatrixUtils;
import org.processmining.plugins.bpmnminer.util.StringUtils;

import java.util.Map;

/**
 * @author Seppe vanden Broucke
 * This class is used to mine dependency/frequency information from a log
 * containing identifier sequences.
 *  
 */
public class DFTable {
	protected IntegerEventLog log;
	
	protected int[] freqMetrics;
	protected int[][] dirsucMetrics;
	protected int[][] l2lMetrics;
	protected int[][] sucMetrics;
	
	private final int taskCount;
	private final Map<Integer, Integer> task2indexes;
	private final int[] index2tasks;
	
	public DFTable(IntegerEventLog l) {
		log = l;
		taskCount = l.getTasks().length;
		index2tasks = l.getTasks();
		task2indexes = l.getIndices();
	}
	
	public int getAMeasure(int task1) {
		return MatrixUtils.getTaskMatrix(task2indexes, getFrequencyMetrics(), task1);
	}

	public int getABMeasure(int task1, int task2) {
		return MatrixUtils.getTaskMatrix(task2indexes, getDirectSuccessionMetrics(), task1, task2);
	}
	
	public int getABAMeasure(int task1, int task2) {
		return MatrixUtils.getTaskMatrix(task2indexes, getL2lMetrics(), task1, task2);
	}
	
	public int getAXBMeasure(int task1, int task2) {
		return MatrixUtils.getTaskMatrix(task2indexes, getSuccessionMetrics(), task1, task2);
	}
	
	
	private int[] getFrequencyMetrics() {
		if (freqMetrics != null)
			return freqMetrics;
		
		int[] table = MatrixUtils.create1DTaskMatrix(taskCount, 0);
		
		for (int[] taskRow : log.getRows()) {
			for (int i = 0; i < taskRow.length; i++) {
				MatrixUtils.increaseTaskMatrix(task2indexes, table, taskRow[i], log.getRowCount(taskRow));
			}
		}
		
		freqMetrics = table;
		return table;
	}

	private int[][] getDirectSuccessionMetrics() {
		if (dirsucMetrics != null)
			return dirsucMetrics;
		
		int[][] table = MatrixUtils.create2DTaskMatrix(taskCount, 0);
		
		for (int[] taskRow : log.getRows()) {
			for (int i = 0; i < taskRow.length - 1; i++) {
				MatrixUtils.increaseTaskMatrix(task2indexes, table, taskRow[i], taskRow[i+1], log.getRowCount(taskRow));
			}
		}
		
		dirsucMetrics = table;
		return table;
	}

	private int[][] getSuccessionMetrics() {
		if (sucMetrics != null)
			return sucMetrics;
		
		int[][] table = MatrixUtils.create2DTaskMatrix(taskCount, 0);
		
		for (int[] taskRow : log.getRows()) {
			for (int i = 0; i < taskRow.length - 1; i++) {
				for (int j = i+1; j < taskRow.length; j++) {
					boolean skip = false;
					for (int k = i+1; k < j; k++)
						if (taskRow[j] == taskRow[k]) {
							skip = true;
							break;
						}
					if (!skip)
						MatrixUtils.increaseTaskMatrix(task2indexes, table, taskRow[i], taskRow[j], log.getRowCount(taskRow));
					if (taskRow[j] == taskRow[i])
						break;
				}
			}
		}
		
		sucMetrics = table;
		return table;
	}

	private int[][] getL2lMetrics() {
		if (l2lMetrics != null)
			return l2lMetrics;
		
		int[][] table = MatrixUtils.create2DTaskMatrix(taskCount, 0);
		
		for (int[] taskRow : log.getRows()) {
			for (int i = 0; i < taskRow.length - 2; i++) {
				if (taskRow[i] != taskRow[i+2])
					continue;
				MatrixUtils.increaseTaskMatrix(task2indexes, table, taskRow[i], taskRow[i+1], log.getRowCount(taskRow));
			}
		}
		
		l2lMetrics = table;
		return table;
	}

	public String toString() {
		String repr 
				= "======= \t ======= \t ======= \t ======= \t ======= \t ======= \t =======\n"
				+ " taskA  \t  taskB  \t   |A|   \t   |B|   \t  |A>B|  \t |A>>B|  \t |A>>>B|\n"
				+ "------- \t ------- \t ------- \t ------- \t ------- \t ------- \t -------\n";
		for (int i = 0; i < index2tasks.length; i++) {
			for (int j = 0; j < index2tasks.length; j++) {
				repr 	+= StringUtils.padDouble(index2tasks[i], 7)
						+" \t "+StringUtils.padDouble(index2tasks[j], 7)
						+" \t "+StringUtils.padDouble(getFrequencyMetrics()[i], 7)
						+" \t "+StringUtils.padDouble(getFrequencyMetrics()[j], 7)
						+" \t "+StringUtils.padDouble(getDirectSuccessionMetrics()[i][j], 7)
						+" \t "+StringUtils.padDouble(getL2lMetrics()[i][j], 7)
						+" \t "+StringUtils.padDouble(getSuccessionMetrics()[i][j], 7)+"\n";
			}
		}
		repr += "------- \t -------- \t -------- \t -------- \t -------- \t -------- \t --------\n";
		return repr;
	}

}
