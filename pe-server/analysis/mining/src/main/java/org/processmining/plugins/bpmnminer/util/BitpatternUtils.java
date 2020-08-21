package org.processmining.plugins.bpmnminer.util;

import java.util.BitSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BitpatternUtils {
	public static boolean isBitSet(int pattern, int position) {
		return (pattern & (1 << position)) == 0 ? false : true;
	}

	public static int countBits(int pattern) {
		pattern = pattern - ((pattern >> 1) & 0x55555555);
		pattern = (pattern & 0x33333333) + ((pattern >> 2) & 0x33333333);
		return (((pattern + (pattern >> 4)) & 0x0F0F0F0F) * 0x01010101) >> 24;
	}

	public static String patternToBits(BitSet bitSet) {
		String repr = "";
		for (int p = 0; p < bitSet.length(); p++)
			repr += bitSet.get(p) ? '1' : '0';
		return repr;
	}
	
	public static int getNrPatterns(int size) {
		return (int) (Math.pow(2, size) - 1);
	}

	public static BitSet getPatternForSingleTask(int task, List<Integer> tasks) {
		BitSet pattern = new BitSet(tasks.size());
		for (int i = 0; i < tasks.size(); i++) {
			int t = tasks.get(i);
			pattern.set(i, task == t);
		}
		return pattern;
	}

	public static Set<Integer> taskSetFromPattern(BitSet pattern, List<Integer> tasks) {
		Set<Integer> taskSet = new HashSet<Integer>();

		for (int i = 0; i < tasks.size(); i++) {
			int task = tasks.get(i);
			if (pattern.get(i))
				taskSet.add(task);
		}

		return taskSet;
	}
	
	public static boolean isTaskCoveredByPattern(int pattern, int task, List<Integer> tasks) {
		for (int i = 0; i < tasks.size(); i++) {
			int t = tasks.get(i);
			if (t == task && isBitSet(pattern, i))
				return true;
		}
		return false;
	}

	public static Set<Integer> getUncoveredTasksByPatterns(Set<BitSet> chosenPatterns, List<Integer> tasks) {
		Set<Integer> uncoveredTasks = new HashSet<Integer>(tasks);
		int nrTasks = tasks.size();
		for (BitSet pattern : chosenPatterns)
			for (int i = 0; i < nrTasks; i++)
				if (pattern.get(i) == true)
					uncoveredTasks.remove(tasks.get(i));
		return uncoveredTasks;
	}
	
	public static boolean isTasksCoveredByPatterns(Set<BitSet> patterns, List<Integer> tasks) {
		return getUncoveredTasksByPatterns(patterns, tasks).size() == 0;
	}

	public static boolean isPatternOverlapping(int nrPositions, int pattern, Set<Integer> otherPatterns) {
		for (int otherPattern : otherPatterns) {
			for (int i = 0; i < nrPositions; i++) {
				if (isBitSet(pattern, i) && isBitSet(otherPattern, i))
					return true;
			}
		}
		return false;
	}
}
