package org.processmining.plugins.bpmnminer.fitness;

import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.types.EventLogTaskMapper;

import java.util.*;

public abstract class BasicFitness extends AbstractFitness {
	public final static int INPUTSELECTIONFIRSTSTRATEGY = 0;
	public final static int INPUTSELECTIONSMALLESTSTRATEGY = 1;
	public final static int INPUTSELECTIONLARGESTSTRATEGY = 2;
	public final static int INPUTSELECTIONRANDOMSTSTRATEGY = 3;
	
	protected int inputSelectionStrategy = INPUTSELECTIONLARGESTSTRATEGY;

	public BasicFitness(EventLogTaskMapper m, CausalNet n) {
		super(m, n);
	}
	
	protected Integer getBestTask(String[] eventSequence,
                                  int[] taskSequence,
                                  int position,
                                  Map<Integer, List<Set<Integer>>> obligations) {
		
		//Integer currentTask = mapper.getIntegerFragment(eventSequence, position);
		Integer currentTask = null;
		String currentEvent = eventSequence[position];
		
		// Determine the real current task if no task could be found
		if (currentTask == null) {
			Set<Integer> matches = mapper.getIntegersEventClass(currentEvent);
			List<Integer> shuffledMatches = new ArrayList<Integer>(matches);
			Collections.shuffle(shuffledMatches);

			if (matches.size() == 0) { // This event is not even present in the net
				return null;
			} else if (matches.size() > 1) { // We try to find the correct/random duplicate
				currentTask = getBestDuplicate(matches, eventSequence, taskSequence, position, obligations);
			} else { // We take the only available event
				currentTask = matches.iterator().next();
			}
		}
		return currentTask;
	}
	
	protected int getBestDuplicate(Set<Integer> duplicates,
                                   String[] eventSequence,
                                   int[] taskSequence,
                                   int position,
                                   Map<Integer, List<Set<Integer>>> obligations) {
		
		List<Integer> originalDuplicates = new ArrayList<Integer>(duplicates);
		
		// Filter out non-enabled
		if (obligations != null) {
			for (int d : originalDuplicates) {
				boolean fireable = isTaskFireable(taskSequence, position, d, obligations);
				if (!fireable)
					duplicates.remove(d);
			}
		}
		
		if (duplicates.size() == 1) {
			return duplicates.iterator().next();
		} else if(duplicates.size() > 0) {
			// Simple heuristic: find first duplicate which might enable the first next possible task
			// and which is enabled by the first previous task
			for (int d : duplicates) {
				boolean prevFound = false;
				boolean nextFound = false;
				for (int i = position+1; i < eventSequence.length; i++) {
					String nextEvent = eventSequence[i];
					Set<Integer> nextTasks = mapper.getIntegersEventClass(nextEvent);
					if (nextTasks == null || nextTasks.size() == 0)
						continue;				
					for (int n : nextTasks) {
						if (net.getOutputTasks(d).contains(n)) {
							nextFound = true;
							break;
						}
					}
					if (nextFound) 
						break;
				}
				for (int i = position-1; i >= 0; i--) {
					String prevEvent = eventSequence[i];
					Set<Integer> prevTasks = mapper.getIntegersEventClass(prevEvent);
					if (prevTasks == null || prevTasks.size() == 0)
						continue;				
					for (int n : prevTasks) {
						if (net.getInputTasks(d).contains(n)) {
							prevFound = true;
							break;
						}
					}
					if (prevFound) 
						break;
				}
				if (prevFound && nextFound)
					return d;
			}
		}
		Collections.shuffle(originalDuplicates);
		return originalDuplicates.get(0);
	}
	
	protected int getLowestRemainingCount(List<Set<Integer>> subsetList) {
		int count = -1;
		for (Set<Integer> set : subsetList) {
			if (set.size() < count || count == -1)
				count = set.size();
		}
		return count == -1 ? 0 : count;
	}
	
	protected boolean isTaskFireable(int[] sequence, int currentPosition, int currentTask,
			Map<Integer, List<Set<Integer>>> obligations) {
		List<Set<Integer>> inputSets = new ArrayList<Set<Integer>>(net.getInputSets(currentTask));
		for (int inputIndex = 0; inputIndex < inputSets.size(); inputIndex++) {
			Set<Integer> inputSet = inputSets.get(inputIndex);
			Set<Integer> foundInputs =
					findTaskInObligations(sequence, currentPosition, currentTask, inputSet, obligations);
			int missing = inputSet.size() - foundInputs.size();
			if (missing == 0)
				return true;
		}
		
		return false;
	}
	
	protected int findBestInputCombination(int[] sequence, int currentPosition, int currentTask, 
			Map<Integer, List<Set<Integer>>> obligations) {
		List<Set<Integer>> inputSets = new ArrayList<Set<Integer>>(net.getInputSets(currentTask));
		List<int[]> bestInputIndexes = new ArrayList<int[]>();
		int bestInputMissing = 0;
		for (int inputIndex = 0; inputIndex < inputSets.size(); inputIndex++) {
			Set<Integer> inputSet = inputSets.get(inputIndex);
			Set<Integer> foundInputs =
					findTaskInObligations(sequence, currentPosition, currentTask, inputSet, obligations);
			int missing = inputSet.size() - foundInputs.size();
			if (bestInputIndexes.size() == 0 || missing < bestInputMissing) {
				bestInputIndexes = new ArrayList<int[]>();
				bestInputMissing = missing;
				bestInputIndexes.add(new int[]{inputIndex, inputSet.size()});
			}
		}
		
		if (bestInputIndexes.size() == 0)
			return -1;
		
		int[] selected;
		switch (inputSelectionStrategy) {
		case INPUTSELECTIONFIRSTSTRATEGY:
			return bestInputIndexes.get(0)[0];
		case INPUTSELECTIONSMALLESTSTRATEGY:
			selected = new int[]{};
			for (int[] i : bestInputIndexes)
				if (selected.length == 0 || i[1] < selected[1])
					selected = i;
			return selected[0];
		case INPUTSELECTIONLARGESTSTRATEGY:
			selected = new int[]{};
			for (int[] i : bestInputIndexes)
				if (selected.length == 0 || i[1] > selected[1])
					selected = i;
			return selected[0];
		case INPUTSELECTIONRANDOMSTSTRATEGY:
			Random randomGenerator = new Random();
			return bestInputIndexes.get(randomGenerator.nextInt(bestInputIndexes.size()))[0];
		}
		
		return -1;
	}
	
	protected Set<Integer> findTaskInObligations(
			int[] sequence,
			int currentPosition,
			int currentTask,
			Set<Integer> inputs,
			Map<Integer, List<Set<Integer>>> obligations) {
		Set<Integer> found = new HashSet<Integer>();
		for (int inputTask : inputs) {
			for (int position = currentPosition - 1; position >= 0; position--) {
				if (sequence[position] == inputTask
						&& isObligationsEnablesTask(obligations.get(position), currentTask)) {
					found.add(inputTask);
				}
			}
		}
		
		return found;
	}
	
	protected boolean isObligationsEnablesTask(List<Set<Integer>> obligations, int task) {
		for (Set<Integer> set : obligations)
			if (set.contains(task))
				return true;
		return false;
	}

}
