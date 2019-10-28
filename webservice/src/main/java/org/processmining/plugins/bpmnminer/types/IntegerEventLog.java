package org.processmining.plugins.bpmnminer.types;

import java.util.*;

public class IntegerEventLog {
	
	protected List<int[]> rows;
	protected Map<Integer, Integer> counts;
	protected Map<Integer, String> labels;
	
	public IntegerEventLog() {
		rows = new ArrayList<int[]>();
		counts = new HashMap<Integer, Integer>();
		labels = new HashMap<Integer, String>();
	}
	
	public void addRow(int[] r) {
		int i = getIndex(r);
		if (i == -1) {
			rows.add(r);
			int n = rows.size() - 1;
			setRowCount(n, 1);
		}else{
			setRowCount(i, getRowCount(i) + 1);
		}
	}
	
	public int[] getRow(int i) {
		return rows.get(i);
	}
	
	public List<int[]> getRows() {
		return rows;
	}

	public void removeRow(int[] r) {
		int i = getIndex(r);
		if (i > -1) {
			rows.remove(i);
			counts.remove(i);
		}
	}
	
	public int getRowCount(int i) {
		return counts.get(i);
	}
	
	public int getRowCount(int[] row) {
		int i = getIndex(row);
		if (i > -1)
			return getRowCount(i);
		return 0;
	}

	public void setRowCount(int i, int c) {
		counts.put(i, c);
	}
	
	public void setRowCount(int[] row, int c) {
		int i = getIndex(row);
		if (i > -1)
			setRowCount(i, c);
	}
	
	public int getIndex(int[] r) {
		for (int i = 0; i < rows.size(); i++)
			if (Arrays.equals(rows.get(i), r))
				return i;
		return -1;
	}

	public String getLabel(int task) {
		return labels.get(task);
	}
	
	public void setLabel(int task, String label) {
		labels.put(task, label);
	}
	
	public int[] getTasks() {
		Set<Integer> usedInts = new HashSet<Integer>();
		for (int row = 0; row < rows.size(); row++)
			for (int task : rows.get(row))
				usedInts.add(task);
		int[] tasks = new int[usedInts.size()];
		int i = 0;
		for (Integer val : usedInts)
			tasks[i++] = val;
		return tasks;
	}
	
	public Map<Integer, Integer> getIndices() {
		Map<Integer, Integer> task2index = new HashMap<Integer, Integer>();
		int[] tasks = getTasks();
		for (int i = 0; i < tasks.length; i++)
			task2index.put(tasks[i], i);
		return task2index;
	}

	public String getSequenceAsString(int index) {
		return getSequenceAsString(getRow(index));
	}

	public String getSequenceAsString(int[] sequence) {
		String repr = "SEQ: ";
		for (int i : sequence)
			if (labels.containsKey(i))
				repr += " -> " + labels.get(i);
		return repr;
	}
	
	public String toString() {
		String repr = "IntegerEventLog (" + rows.size() + " rows, " + getTasks().length + " tasks):\n";
		for (int t : getTasks())
			repr += t + " ==> " + getLabel(t) + "\n";
		return repr;
	}

}
