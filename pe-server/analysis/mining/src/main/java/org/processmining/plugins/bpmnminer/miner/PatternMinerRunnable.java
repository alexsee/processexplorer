package org.processmining.plugins.bpmnminer.miner;

import org.processmining.plugins.bpmnminer.dependencygraph.DependencyNet;
import org.processmining.plugins.bpmnminer.types.IntegerEventLog;

import java.util.*;

public class PatternMinerRunnable implements Runnable {
	int task;
	
	Set<Integer> longDistanceTasks;
	IntegerEventLog log;
	boolean direction;
	Map<BitSet, Integer> foundPatterns;
	DependencyNet net;
	List<Integer> inputsOrOutputs;
	
	public PatternMinerRunnable(int t, Set<Integer> ldTasks, IntegerEventLog l, DependencyNet n, boolean d) {
		task = t;
		longDistanceTasks = ldTasks;
		log = l;
		direction = d;
		net = n;
		inputsOrOutputs = (direction)
				? new ArrayList<Integer>(net.getTaskInputs(task))
				: new ArrayList<Integer>(net.getTaskOutputs(task));
	}
	
	public Map<BitSet, Integer> getFoundPatterns() {
		return foundPatterns;
	}
	
	public List<Integer> getConnectedTasks() {
		return inputsOrOutputs;
	}

	public void run() {
		foundPatterns = findPatterns(task, inputsOrOutputs, longDistanceTasks, direction);
	}
	
	private Map<BitSet, Integer> findPatterns(int sjTask, List<Integer> connectedTasks, Set<Integer> ldTasks, boolean isTypeInput) {
		Map<BitSet, Integer> pattern2Count = new HashMap<BitSet, Integer>();
		for (int[] row : log.getRows()) {
			for (int p = 0; p < row.length; p++) {
				int task = row[p];
				if (task != sjTask)
					continue;
				BitSet pattern = isTypeInput 
						? getInputPattern(row, p, connectedTasks, ldTasks) 
						: getOutputPattern(row, p, connectedTasks, ldTasks);
				if (pattern.cardinality() == 0)
					continue;
				if (!pattern2Count.keySet().contains(pattern))
					pattern2Count.put(pattern, 0);
				pattern2Count.put(pattern, pattern2Count.get(pattern) + log.getRowCount(row));
			}
		}
		return pattern2Count;
	}
	
	private BitSet getInputPattern(int[] row, int taskPos, List<Integer> connectedTasks, Set<Integer> ldTasks) {
		BitSet pattern = new BitSet(connectedTasks.size());
		for (int i = 0; i < connectedTasks.size(); i++) {
			int htTask = connectedTasks.get(i);
			boolean inputNearestToOriginating = 
					isTaskNearestOutput(row, taskPos, htTask, net.getTaskOutputs(htTask), ldTasks.contains(htTask));
			pattern.set(i, inputNearestToOriginating);
		}
		return pattern;
	}
	
	private BitSet getOutputPattern(int[] row, int taskPos, List<Integer> connectedTasks, Set<Integer> ldTasks) {
		BitSet pattern = new BitSet(connectedTasks.size());
		for (int i = 0; i < connectedTasks.size(); i++) {
			int htTask = connectedTasks.get(i);
			boolean outputNearestToOriginating = 
					isTaskNearestInput(row, taskPos, htTask, net.getTaskInputs(htTask), ldTasks.contains(htTask));
			pattern.set(i, outputNearestToOriginating);
		}
		return pattern;
	}
	
	private boolean isTaskNearestInput(int[] row, int splitPos, int tailTask, Set<Integer> otherInputs, boolean ignoreNearest) {
		// Find position of tailTask after splitPos
		int tailPos = -1;
		for (int position = splitPos + 1; position < row.length; position++) {
			if (row[position] == row[splitPos])
				return false;
			if (row[position] == tailTask) {
				tailPos = position;
				break;
			}
		}
		if (tailPos == -1) // Tail task couldn't be found
			return false;

		// Check if tailTask is closer to another Input
		if (!ignoreNearest)
			for (int position = tailPos - 1; position > splitPos; position--)
				if (otherInputs.contains(row[position]))
					return false;

		return true;
	}

	private boolean isTaskNearestOutput(int[] row, int joinPos, int headTask, Set<Integer> otherOutputs, boolean ignoreNearest) {
		// Find position of headTask before joinPos
		int headPos = -1;
		for (int position = joinPos - 1; position >= 0; position--) {
			if (row[position] == row[joinPos])
				return false;
			if (row[position] == headTask) {
				headPos = position;
				break;
			}
		}
		if (headPos == -1) // Head task couldn't be found
			return false;

		// Check if headTask is closer to another Output
		if (!ignoreNearest)
			for (int position = headPos + 1; position < joinPos; position++)
				if (otherOutputs.contains(row[position]))
					return false;

		return true;
	}
}
