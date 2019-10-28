package org.processmining.plugins.bpmnminer.fitness;

import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.types.EventLogTaskMapper;

import java.util.*;

public class ICSFitness extends BasicFitness {

	protected int missingTokens;
	protected int remainingTokens;
	
	protected int playedTasks;
	protected int parsedTasks;
	protected int unparsedTasks;
	
	protected int playedSequences;
	protected int parsedSequences;
	protected int missingSequences;
	protected int remainingSequences;
	
	public ICSFitness(EventLogTaskMapper m, CausalNet n) {
		super(m, n);
	}
	
	public void reset() {
		missingTokens = 0;
		remainingTokens = 0;
		playedTasks = 0;
		parsedTasks = 0;
		unparsedTasks = 0;
		playedSequences = 0;
		parsedSequences = 0;
		missingSequences = 0;
		remainingSequences = 0;
	}
	
	public double getFitness() {
		double punishment = 
					((double)missingTokens	 / ((double)playedSequences - (double)missingSequences + 1D)) 
				+	((double)remainingTokens / ((double)playedSequences - (double)remainingSequences + 1D));
		return ((double)parsedTasks - punishment) / (double)playedTasks;
	}

	public void replayTrace(XTrace trace, int traceCount) {
		playedSequences += traceCount;
		boolean hadMissingToken = false;
		boolean hadRemainingToken = false;
		
		String[] eventrow = mapper.classifyTrace(trace);
		int[] taskrow = mapper.getIntegerTrace(trace);
		Map<Integer, List<Set<Integer>>> obligations = new HashMap<Integer, List<Set<Integer>>>();
		for (int i = 0; i < taskrow.length; i++)
			obligations.put(i, new ArrayList<Set<Integer>>());
		
		for (int i = 0; i < taskrow.length; i++) {
			playedTasks += traceCount;
			
			int currentPosition = i;
			Integer currentTask = getBestTask(eventrow, taskrow, currentPosition, obligations);	
			taskrow[currentPosition] = (currentTask == null) ? -9999 : currentTask;
				
			// Now fire
			if (currentTask == null) {
				unparsedTasks += traceCount;
				missingTokens += traceCount;
				hadMissingToken = true;
			} else {	
				List<Set<Integer>> inputSets = new ArrayList<Set<Integer>>(net.getInputSets(currentTask));
				Set<Integer> bestInputSet = new HashSet<Integer>();
				if (inputSets.size() > 0) {
					int bestInputSetIndex = findBestInputCombination(taskrow, currentPosition, currentTask, obligations);
					bestInputSet = inputSets.get(bestInputSetIndex);
				}	
				int inputProblemCount = 0;
				for (int inputTask : bestInputSet) {
					boolean couldEnable = false;
					for (int position = currentPosition - 1; position >= 0; position--) {
						List<Set<Integer>> previousTaskObligations = obligations.get(position);		
						if (taskrow[position] != inputTask
								|| !isObligationsEnablesTask(previousTaskObligations, currentTask))
							continue;
						
						for (int outputIndex = previousTaskObligations.size() - 1; outputIndex >= 0; outputIndex--) {
							// Remove the outputset completely if it does not enable this task, else just
							// remove the task itself from the outputset
							if (!previousTaskObligations.get(outputIndex).contains(currentTask))
								previousTaskObligations.remove(outputIndex);
							else
								previousTaskObligations.get(outputIndex).remove(currentTask);
						}
						obligations.put(position, previousTaskObligations);
						couldEnable = true;
						break;
					}
					if (!couldEnable)
						inputProblemCount++;
				}	
				
				if (inputProblemCount > 0) {
					unparsedTasks += traceCount;
					missingTokens += inputProblemCount * traceCount;
					hadMissingToken = true;
				} else{
					parsedTasks += traceCount;
				}
			}
			
			
			// Set obligations for this position
			List<Set<Integer>> outputSets = net.getOutputSets(taskrow[currentPosition]);
			List<Set<Integer>> outputSetsCopy = new ArrayList<Set<Integer>>();
			for (Set<Integer> set : outputSets)
				outputSetsCopy.add(new HashSet<Integer>(set));
			obligations.put(currentPosition, outputSetsCopy);
		} // For task loop
		
		for (int i = 0; i < taskrow.length; i++) {
			int lowest = getLowestRemainingCount(obligations.get(i));
			remainingTokens += traceCount * lowest;
			if (lowest > 0)
				hadRemainingToken = true;
		}
		
		if (hadMissingToken)
			missingSequences += traceCount;
		if (hadRemainingToken)
			remainingSequences += traceCount;
		if (!hadMissingToken && !hadRemainingToken)
			parsedSequences += traceCount;
	}

	
}
