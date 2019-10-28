package org.processmining.plugins.bpmnminer.causalnet;

import org.processmining.plugins.bpmnminer.types.IntegerEventLog;

import java.util.*;

public class CausalNet {
	private IntegerEventLog log;

	private Map<Integer, List<Set<Integer>>> inputSets;
	private Map<Integer, List<Set<Integer>>> outputSets;
	private Set<Integer> tasks;
	private Map<Integer, String> labels;
	
	public CausalNet() {
		clear();
	}

	public CausalNet(IntegerEventLog l) {
		this();
		if (l != null) {
			log = l;	
			for (int task : l.getTasks()) {
				addTask(task);
				setLabel(task, l.getLabel(task));
			}
		}
	}

	private void clear() {
		labels = new HashMap<Integer, String>();
		tasks = new HashSet<Integer>();
		inputSets = new HashMap<Integer, List<Set<Integer>>>();
		outputSets = new HashMap<Integer, List<Set<Integer>>>();
	}

	public IntegerEventLog getLog() {
		return log;
	}

	public Set<Integer> getTasks() {
		return tasks;
	}

	public void addTask(int task) {
		tasks.add(task);
		if (!inputSets.keySet().contains(task))
			inputSets.put(task, new ArrayList<Set<Integer>>());
		if (!outputSets.keySet().contains(task))
			outputSets.put(task, new ArrayList<Set<Integer>>());
	}

	public void removeTask(int task) {
		tasks.remove(task);
		inputSets.remove(task);
		outputSets.remove(task);
	}
	
	public String getLabel(int task) {
		if (labels.containsKey(task))
			return labels.get(task);
		return task + " (no label)";
	}
	
	public void setLabel(int task, String label) {
		labels.put(task, label);
	}

	public void addOutputSet(int task, Collection<Integer> outputSet) {
		Set<Integer> set = new HashSet<Integer>(outputSet);
		outputSets.get(task).add(set);
	}

	public void addOutputSet(int task, int[] outputSet) {
		addOutputSet(task, intArrayToSet(outputSet));
	}

	public void addInputSet(int task, Collection<Integer> inputSet) {
		Set<Integer> set = new HashSet<Integer>(inputSet);
		inputSets.get(task).add(set);
	}

	public void addInputSet(int task, int[] inputSet) {
		addOutputSet(task, intArrayToSet(inputSet));
	}

	public List<Set<Integer>> getOutputSets(int task) {
		if (outputSets.get(task) == null) return new ArrayList<Set<Integer>>();
		return outputSets.get(task);
	}

	public List<Set<Integer>> getInputSets(int task) {
		if (inputSets.get(task) == null) return new ArrayList<Set<Integer>>();
		return inputSets.get(task);
	}

	public Set<Integer> getOutputTasks(int task) {
		Set<Integer> tasks = new HashSet<Integer>();
		for (Set<Integer> taskSet : getOutputSets(task)) {
			for (int t : taskSet)
				tasks.add(t);
		}
		return tasks;
	}

	public Set<Integer> getInputTasks(int task) {
		Set<Integer> tasks = new HashSet<Integer>();
		for (Set<Integer> taskSet : getInputSets(task)) {
			for (int t : taskSet)
				tasks.add(t);
		}
		return tasks;
	}

	public void removeInputSet(int task, int index) {
		inputSets.get(task).remove(index);
	}

	public void removeInputSet(int task, Collection<Integer> inputSet) {
		Set<Integer> set = new HashSet<Integer>(inputSet);
		inputSets.get(task).remove(set);
	}

	public void removeInputSet(int task, int[] inputSet) {
		removeInputSet(task, intArrayToSet(inputSet));
	}

	public void removeOutputSet(int task, int index) {
		outputSets.get(task).remove(index);
	}

	public void removeOutputSet(int task, Collection<Integer> outputSet) {
		Set<Integer> set = new HashSet<Integer>(outputSet);
		outputSets.get(task).remove(set);
	}

	public void removeOutputSet(int task, int[] outputSet) {
		removeOutputSet(task, intArrayToSet(outputSet));
	}

	public void removeInputSets(int task) {
		inputSets.get(task).clear();
	}

	public void removeOutputSets(int task) {
		outputSets.get(task).clear();
	}

	private Set<Integer> intArrayToSet(int[] array) {
		Set<Integer> set = new HashSet<Integer>();
		for (int a : array)
			set.add(a);
		return set;
	}

	public String toString() {
		String repr = "CausalNet with " + tasks.size() + " tasks, " + inputSets.size() + " input sets and "
				+ outputSets.size() + " output sets\n";

		repr += "- Input sets:\n";
		for (int task : tasks)
			repr += "\t" + task + ":\t" + inputSets.get(task) + "\n";
		repr += "- Output sets:\n";
		for (int task : tasks)
			repr += "\t" + task + ":\t" + outputSets.get(task) + "\n";

		return repr;
	}
}
