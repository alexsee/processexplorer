package org.processmining.plugins.bpmnminer.dependencygraph;

import org.processmining.plugins.bpmnminer.types.IntegerEventLog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DependencyNet {
	private IntegerEventLog log;
	
	private Map<Integer, Set<Integer>> arcs;
	private Set<Integer> tasks;
	private Map<Integer, String> labels;
	
	private int acceptedStartTask;
	private int acceptedEndTask;
	
	public DependencyNet() {
		clear();
	}
	
	public DependencyNet(IntegerEventLog l) {
		this();
		if (l != null) {
			log = l;
			for (int task : l.getTasks()) {
				addTask(task);
				setLabel(task, l.getLabel(task));
			}
		}
	}
	
	public void clear() {
		labels = new HashMap<Integer, String>();
		tasks = new HashSet<Integer>();
		arcs = new HashMap<Integer, Set<Integer>>();
		acceptedStartTask = -1;
		acceptedEndTask = -1;
	}
	
	public IntegerEventLog getLog() {
		return log;
	}

	public Set<Integer> getTasks() {
		return tasks;
	}

	public void addTask(int task) {
		tasks.add(task);
		if (!arcs.keySet().contains(task))
			arcs.put(task, new HashSet<Integer>());
	}
	
	public void removeTask(int task) {
		tasks.remove(task);
		arcs.remove(task);
	}
	
	public String getLabel(int task) {
		if (labels.containsKey(task))
			return labels.get(task);
		return null;
	}
	
	public void setLabel(int task, String label) {
		labels.put(task, label);
	}
	
	public int getStartTask() {
		return acceptedStartTask;
	}

	public void setStartTask(int acceptedStartTask) {
		this.acceptedStartTask = acceptedStartTask;
	}
	
	public int getEndTask() {
		return acceptedEndTask;
	}

	public void setEndTask(int acceptedEndTask) {
		this.acceptedEndTask = acceptedEndTask;
	}

	public void setArc(int taskA, int taskB) {
		setArc(taskA, taskB, true);
	}
	
	public void setArc(int taskA, int taskB, boolean enable) {
		if (enable)
			arcs.get(taskA).add(taskB);
		else
			arcs.get(taskA).remove(taskB);
	}
	
	public boolean isArc(int taskA, int taskB) {
		return arcs.get(taskA).contains(taskB);
	}
	
	public boolean isL1Loop(int taskA) {
		return isArc(taskA, taskA);
	}
	
	public boolean isL2Loop(int taskA, int taskB) {
		return taskA != taskB && isArc(taskA, taskB) && isArc(taskB, taskA);
	}
	
	public int getTaskInputDependencyCount(int task) {
		int count = 0;
		for (int t : tasks)
			if (isArc(t, task))
				count++;
		return count;
	}

	public int getTaskOutputDependencyCount(int task) {
		int count = 0;
		for (int t : tasks)
			if (isArc(task, t))
				return count++;
		return count;
	}
	
	public Set<Integer> getTaskInputs(int task) {
		Set<Integer> set = new HashSet<Integer>();
		for (int t : tasks)
			if (isArc(t, task))
				set.add(t);
		return set;
	}

	public Set<Integer> getTaskOutputs(int task) {
		Set<Integer> set = new HashSet<Integer>();
		for (int t : tasks)
			if (isArc(task, t))
				set.add(t);
		return set;
	}
	
	public Set<Integer> getUnconnectedTasksFromStart() {
		Set<Integer> unconnected = new HashSet<Integer>();
		
		Set<Integer> todo = new HashSet<Integer>();
		todo.add(getStartTask());
		Set<Integer> visited = new HashSet<Integer>();
		visited.add(getStartTask());
		
		while (!todo.isEmpty()) {
			int t = todo.iterator().next();
			todo.remove(t);
			Set<Integer> outputs = getTaskOutputs(t);
			outputs.removeAll(visited);
			visited.addAll(outputs);
			todo.addAll(outputs);
		}
				
		for (int t : tasks) {
			if (!visited.contains(t))
				unconnected.add(t);
		}
		return unconnected;
	}
	
	public Set<Integer> getUnconnectedTasksFromEnd() {
		Set<Integer> unconnected = new HashSet<Integer>();
		
		Set<Integer> todo = new HashSet<Integer>();
		todo.add(getEndTask());
		Set<Integer> visited = new HashSet<Integer>();
		visited.add(getEndTask());
		
		while (!todo.isEmpty()) {
			int t = todo.iterator().next();
			todo.remove(t);
			Set<Integer> inputs = getTaskInputs(t);
			inputs.removeAll(visited);
			visited.addAll(inputs);
			todo.addAll(inputs);
		}
		
		for (int t : tasks) {
			if (!visited.contains(t))
				unconnected.add(t);
		}
		return unconnected;
	}
	
	public boolean isPathPossibleWithoutVisiting(int startTask, int endTask, int a) {
		Set<Integer> todo = new HashSet<Integer>();
		todo.add(startTask);
		Set<Integer> visited = new HashSet<Integer>();
		visited.add(startTask);
		
		while (!todo.isEmpty()) {
			int t = todo.iterator().next();
			todo.remove(t);
			if (t == a)
				continue;
			visited.add(t);
			Set<Integer> outputs = getTaskOutputs(t);
			outputs.removeAll(visited);
			todo.addAll(outputs);
			if (visited.contains(endTask))
				return true;
		}
		
		return false;
	}

	public String toString() {
		String repr = "f\\t\t";
		for (int t : tasks) {
			String v = (getEndTask() != t) ? "" : "e";
			repr += "| " + t + v + " \t";
		}
		repr += "\n---\t";
		for (@SuppressWarnings("unused") int t : tasks)
			repr += "|---\t";
		for (int t : tasks) {
			String v = (getStartTask() != t) ? "" : "s";
			repr += "\n" + t + v + "\t";
			for (int s : tasks) {
				v = (!isArc(t, s)) ? "   " : " * ";
				repr += "|" + v + "\t";
			}
		}
		return repr;
	}
	
}
