package org.processmining.plugins.bpmnminer.miner;

import org.deckfour.xes.model.XLog;
import org.processmining.plugins.bpmnminer.causalnet.CausalNet;
import org.processmining.plugins.bpmnminer.dependencygraph.DependencyNet;
import org.processmining.plugins.bpmnminer.types.EventLogTaskMapper;
import org.processmining.plugins.bpmnminer.types.IntegerEventLog;
import org.processmining.plugins.bpmnminer.types.MinerSettings;
import org.processmining.plugins.bpmnminer.types.RelationalMetrics;
import org.processmining.plugins.bpmnminer.util.BitpatternUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;

public class FodinaMiner {

	private static Logger logger = LoggerFactory.getLogger(FodinaMiner.class);

	public static final boolean DEBUG_PATTERNS = false;

	protected final IntegerEventLog log;
	protected final RelationalMetrics metrics;
	protected final MinerSettings settings;

	private DependencyNet dependencyNet;
	private CausalNet causalNet;

	private EventLogTaskMapper mapper;

	public static FodinaMiner create(XLog log, MinerSettings settings) {
		EventLogTaskMapper mapper = new EventLogTaskMapper(log, settings.classifier);
		mapper.setup(settings.backwardContextSize,
				settings.forwardContextSize,
				settings.useUniqueStartEndTasks,
				settings.collapseL1l,
				settings.duplicateThreshold);
		IntegerEventLog ieLog = mapper.getIntegerLog();
		FodinaMiner miner = new FodinaMiner(ieLog, settings);
		miner.mapper = mapper;
		return miner;
	}

	public FodinaMiner(IntegerEventLog l, MinerSettings s) {
		this(l, null, s);
	}

	public FodinaMiner(IntegerEventLog l, RelationalMetrics m, MinerSettings s) {
		log = l;
		metrics = (m == null) ? new RelationalMetrics(l, s) : m;
		settings = s;
	}

	public EventLogTaskMapper getMapper() {
		return this.mapper;
	}

	public void clear() {
		dependencyNet = new DependencyNet(log);
	}

	public CausalNet getCausalNet() {
		return causalNet;
	}

	public DependencyNet getDependencyNet() {
		return dependencyNet;
	}

	public CausalNet mine() {
		mineCausalNet();

		if (DEBUG_PATTERNS)
			debugPatterns();

		return getCausalNet();
	}

	public void mineDependencyNet() {
		clear();
		logger.trace("FODINA: Starting");

		// Step 1, 2, 3: mine basic dependency graph
		mineCausalityDependencies();
		mineL1loops();
		mineL2loops();
		logger.trace("FODINA: Basic dependency graph mined");

		// Step 4: set start / end
		mineStartEnd();
		logger.trace("FODINA: Start/end discovered");

		// Step 5: Resolve binary conflicts (optional)
		if (settings.preferAndToL2l)
			relinkBinaryConflicts();
		logger.trace("FODINA: Binary conflicts step done");

		// Step 6: all connected (optional)
		if (settings.useAllConnectedHeuristics)
			mineConnectingDependencies();
		logger.trace("FODINA: All connected step done");
	}

	public void mineCausalNet() {
		mineDependencyNet();

		// Step 7: calculate split / joins, optionally with long distance dependencies (optional)
		if (settings.useLongDistanceDependency) {
			Map<Integer, Set<Integer>> longDistanceDependencies = mineLongDistanceDependencies();
			mineSemanticCausalNet(longDistanceDependencies);
		} else {
			mineSemanticCausalNet();
		}
		logger.trace("FODINA: Patterns mined");
	}

	private void debugPatterns() {
		Map<Integer, Set<Integer>> longDistanceDependencies = new HashMap<Integer, Set<Integer>>();
		for (int t : dependencyNet.getTasks())
			longDistanceDependencies.put(t, new HashSet<Integer>());
		if (settings.useLongDistanceDependency)
			longDistanceDependencies = mineLongDistanceDependencies();
		for (int t : dependencyNet.getTasks()) {
			List<Integer> outputs = new ArrayList<Integer>(dependencyNet.getTaskOutputs(t));
			List<Integer> inputs = new ArrayList<Integer>(dependencyNet.getTaskInputs(t));
			Map<BitSet, Integer> outputPatterns = debugFindPatterns(t, outputs,longDistanceDependencies, false);
			Map<BitSet, Integer> inputPatterns = debugFindPatterns(t, inputs, longDistanceDependencies, true);
			double total;

			System.err.println(" ================ Task: " + t + " ================ ");

			System.err.println(" ----- output patterns for tasks: " + outputs + " -----");
			total = 0D;
			for (Entry<BitSet, Integer> entry : outputPatterns.entrySet()) {
				double frequency = calculatePatternFrequency(t, entry.getValue());
				total += frequency;
				System.err.println("   - Pattern: " + ":\t" + BitpatternUtils.patternToBits(entry.getKey()) + "\t"
						+ entry.getValue() + "\t" + frequency);
			}
			System.err.println("   ----- TOTAL = " + total);
			System.err.println(" ----- chosen output patterns for tasks: " + outputs + " -----");
			Set<BitSet> chosenOutputPatterns = selectPatterns(t, outputPatterns, outputs);
			for (BitSet entry : chosenOutputPatterns) {
				System.err.println("   - Pattern: " + ":\t" + BitpatternUtils.patternToBits(entry));
			}

			System.err.println(" ----- input patterns for tasks: " + inputs + " -----");
			total = 0D;
			for (Entry<BitSet, Integer> entry : inputPatterns.entrySet()) {
				double frequency = calculatePatternFrequency(t, entry.getValue());
				total += frequency;
				System.err.println("   - Pattern: " + ":\t" + BitpatternUtils.patternToBits(entry.getKey()) + "\t"
						+ entry.getValue() + "\t" + frequency);
			}
			System.err.println("   ----- TOTAL = " + total);
			System.err.println(" ----- chosen input patterns for tasks: " + inputs + " -----");
			Set<BitSet> chosenInputPatterns = selectPatterns(t, inputPatterns, inputs);
			for (BitSet entry : chosenInputPatterns) {
				System.err.println("   - Pattern: " + ":\t" + BitpatternUtils.patternToBits(entry));
			}
		}
	}

	private Map<BitSet, Integer> debugFindPatterns(int t, List<Integer> outputs, Map<Integer, Set<Integer>> longDistanceDependencies, boolean b) {
		PatternMinerRunnable task = new PatternMinerRunnable(t,
				b ? getIncomingLongDistanceTasks(t, longDistanceDependencies) :
						getOutgoingLongDistanceTasks(t, longDistanceDependencies),
				log, dependencyNet, b);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		executor.execute(task);
		executor.shutdown();
		try {
			while (!executor.awaitTermination(10, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return task.getFoundPatterns();
	}

	private void mineCausalityDependencies() {
		for (int a : dependencyNet.getTasks()) {
			for (int b : dependencyNet.getTasks()) {
				if (metrics.getDependencyMeasure(a, b) >= settings.dependencyThreshold)
					dependencyNet.setArc(a, b);
			}
		}
	}

	private void mineL1loops() {
		for (int a : dependencyNet.getTasks()) {
			if (metrics.getL1lMeasure(a) >= settings.l1lThreshold)
				dependencyNet.setArc(a, a);
		}
	}

	private void mineL2loops() {
		for (int a : dependencyNet.getTasks()) {
			for (int b : dependencyNet.getTasks()) {
				if (a == b)
					continue;
				if (settings.preventL2lWithL1l && dependencyNet.isArc(a, a) && dependencyNet.isArc(b, b))
					continue;
				if (metrics.getL2lMeasure(a, b) >= settings.l2lThreshold) {
					dependencyNet.setArc(a, b);
					dependencyNet.setArc(b, a);
				}
			}
		}
	}

	private void mineStartEnd() {
		if (settings.useUniqueStartEndTasks) {
			int[] row = log.getRow(0);
			dependencyNet.setStartTask(row[0]);
			dependencyNet.setEndTask(row[row.length - 1]);
		} else {
			int bestStartCount = -1;
			int bestEndCount = -1;
			for (int a : dependencyNet.getTasks()) {
				int startCount = 0;
				int endCount = 0;
				for (int[] row : log.getRows()) {
					if (row[0] == a)
						startCount += log.getRowCount(row);
					if (row[row.length - 1] == a)
						endCount += log.getRowCount(row);
				}
				if (bestStartCount == -1 || startCount > bestStartCount) {
					bestStartCount = startCount;
					dependencyNet.setStartTask(a);
				}
				if (bestEndCount == -1 || endCount > bestEndCount) {
					bestEndCount = endCount;
					dependencyNet.setEndTask(a);
				}
			}
		}
		for (int a : dependencyNet.getTasks()) {
			dependencyNet.setArc(a, dependencyNet.getStartTask(), false);
			dependencyNet.setArc(dependencyNet.getEndTask(), a, false);
		}
	}

	private void mineConnectingDependencies() {
		boolean faults = true;
		Set<Integer> previousUnconnectedFromStart = new HashSet<Integer>();
		Set<Integer> previousUnconnectedFromEnd = new HashSet<Integer>();
		while (faults) {
			Set<Integer> unconnectedFromStart = dependencyNet.getUnconnectedTasksFromStart();
			Set<Integer> unconnectedFromEnd = dependencyNet.getUnconnectedTasksFromEnd();
			faults = !unconnectedFromStart.isEmpty() || !unconnectedFromEnd.isEmpty();
			if (!faults)
				break;
			if (previousUnconnectedFromStart.equals(unconnectedFromStart)
					&& (previousUnconnectedFromEnd.equals(unconnectedFromEnd))){
				logger.warn("WARNING: The dependency net can not be fully connected"
						+ " without using artificial source and sink tasks");
				break;
			}
			acceptNextBestDependency(unconnectedFromStart, unconnectedFromEnd);
			previousUnconnectedFromStart.addAll(unconnectedFromStart);
			previousUnconnectedFromEnd.addAll(unconnectedFromEnd);
		}
	}

	private void mineSemanticCausalNet() {
		Map<Integer, Set<Integer>> longDistanceDependencies = new HashMap<Integer, Set<Integer>>();
		for (int t : dependencyNet.getTasks())
			longDistanceDependencies.put(t, new HashSet<Integer>());
		mineSemanticCausalNet(longDistanceDependencies);
	}

	private void mineSemanticCausalNet(Map<Integer, Set<Integer>> longDistanceDependencies) {
		Map<Integer, PatternMinerRunnable> taskMapI, taskMapO;
		BlockingQueue<Runnable> worksQueue;
		ThreadPoolExecutor executor;

		causalNet = new CausalNet(log);
		for (int t : dependencyNet.getTasks()) {
			causalNet.addTask(t);
			causalNet.setLabel(t, dependencyNet.getLabel(t));
		}

		// Find patterns using Executor
		taskMapI = new HashMap<Integer, PatternMinerRunnable>();
		taskMapO = new HashMap<Integer, PatternMinerRunnable>();
		worksQueue = new ArrayBlockingQueue<Runnable>(10);
		executor = new ThreadPoolExecutor(10, 	// core size
				20, 	// max size
				1, 		// keep alive time
				TimeUnit.MINUTES, 	// keep alive time units
				worksQueue 			// the queue to use
		);

		for (int t : dependencyNet.getTasks()) {
			int nrTasksO = dependencyNet.getTaskOutputs(t).size();
			int nrTasksI = dependencyNet.getTaskInputs(t).size();
			if (nrTasksO > 0) {
				PatternMinerRunnable task = new PatternMinerRunnable(t,
						getOutgoingLongDistanceTasks(t, longDistanceDependencies),
						log, dependencyNet, false);
				taskMapO.put(t, task);
			}
			if (nrTasksI > 0) {
				PatternMinerRunnable task = new PatternMinerRunnable(t,
						getIncomingLongDistanceTasks(t, longDistanceDependencies),
						log, dependencyNet, true);
				taskMapI.put(t, task);
			}
		}

		for (PatternMinerRunnable task : taskMapI.values()) {
			while (executor.getQueue().remainingCapacity() == 0);
			executor.execute(task);
		}
		for (PatternMinerRunnable task : taskMapO.values()) {
			while (executor.getQueue().remainingCapacity() == 0);
			executor.execute(task);
		}

		executor.shutdown();
		try {
			while (!executor.awaitTermination(10, TimeUnit.SECONDS));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		for (int t : dependencyNet.getTasks()) {
			int nrTasksO = dependencyNet.getTaskOutputs(t).size();
			int nrTasksI = dependencyNet.getTaskInputs(t).size();
			if (nrTasksO > 0) {
				Map<BitSet, Integer> foundPatterns = taskMapO.get(t).getFoundPatterns();
				Set<BitSet> chosenPatterns = selectPatterns(t, foundPatterns, taskMapO.get(t).getConnectedTasks());
				for (BitSet pattern : chosenPatterns)
					causalNet.addOutputSet(t,
							BitpatternUtils.taskSetFromPattern(pattern, taskMapO.get(t).getConnectedTasks()));
			}
			if (nrTasksI > 0) {
				Map<BitSet, Integer> foundPatterns = taskMapI.get(t).getFoundPatterns();
				Set<BitSet> chosenPatterns = selectPatterns(t, foundPatterns, taskMapI.get(t).getConnectedTasks());
				for (BitSet pattern : chosenPatterns)
					causalNet.addInputSet(t,
							BitpatternUtils.taskSetFromPattern(pattern, taskMapI.get(t).getConnectedTasks()));
			}
		}
	}

	private Map<Integer, Set<Integer>> mineLongDistanceDependencies() {
		Map<Integer, Set<Integer>> longDistanceDependencies = new HashMap<Integer, Set<Integer>>();
		int start = dependencyNet.getStartTask();
		int end = dependencyNet.getEndTask();
		for (int a : dependencyNet.getTasks()) {
			longDistanceDependencies.put(a, new HashSet<Integer>());
			for (int b : dependencyNet.getTasks()) {
				if ((metrics.getLdMeasure(a, b) >= settings.longDistanceThreshold)
						&& (dependencyNet.isPathPossibleWithoutVisiting(start, end, a))
						&& (dependencyNet.isPathPossibleWithoutVisiting(start, end, b))
						&& (dependencyNet.isPathPossibleWithoutVisiting(a, end, b))) {
					dependencyNet.setArc(a, b);
					longDistanceDependencies.get(a).add(b);
				}
			}
		}
		return longDistanceDependencies;
	}

	private void relinkBinaryConflicts() {
		// Relink L2L X->A->B->A->Y loops where A only has one non L1L input and output
		for (int a : dependencyNet.getTasks()) {
			for (int b : dependencyNet.getTasks()) {

				if (!dependencyNet.isL2Loop(a, b))
					continue;

				for (int c : dependencyNet.getTasks()) {
					if (c == a || c == b)
						continue;
					if (dependencyNet.isArc(c, a) || dependencyNet.isArc(c, b)) {
						dependencyNet.setArc(c, a);
						dependencyNet.setArc(c, b);
					}
					if (dependencyNet.isArc(a, c) || dependencyNet.isArc(b, c)) {
						dependencyNet.setArc(a, c);
						dependencyNet.setArc(b, c);
					}
				}


				if (metrics.getBasicRelations().getABAMeasure(a, b) > 0)
					dependencyNet.setArc(a, a);
				if (metrics.getBasicRelations().getABAMeasure(b, a) > 0)
					dependencyNet.setArc(b, b);
				dependencyNet.setArc(a, b, false);
				dependencyNet.setArc(b, a, false);
			}
		}
	}
	
	/*
	private void relinkBinaryConflictsStrict() {
		// Relink L2L X->A->B->A->Y loops where A only has one non L1L input and output
		for (int a : dependencyNet.getTasks()) {
			for (int b : dependencyNet.getTasks()) {

				if (!dependencyNet.isL2Loop(a, b))
					continue;
				if (!isSingleL2Loop(a, b))
					continue;

				Set<Integer> aInputs = dependencyNet.getTaskInputs(a);
				Set<Integer> aOutputs = dependencyNet.getTaskOutputs(a);
				Set<Integer> bInputs = dependencyNet.getTaskInputs(b);
				Set<Integer> bOutputs = dependencyNet.getTaskOutputs(b);

				// Remove self l1l and l2l
				aInputs.remove(b);
				aOutputs.remove(b);
				aInputs.remove(a);
				aOutputs.remove(a);
				bInputs.remove(b);
				bOutputs.remove(b);
				bInputs.remove(a);
				bOutputs.remove(a);

				if (aInputs.size() != 1 || aOutputs.size() != 1)
					continue;

				if (bInputs.size() != 0 || bOutputs.size() != 0)
					continue;

				int newInput = aInputs.iterator().next();
				int newOutput = aOutputs.iterator().next();
				dependencyNet.setArc(a, b, false);
				dependencyNet.setArc(b, a, false);
				dependencyNet.setArc(newInput, b);
				dependencyNet.setArc(b, newOutput);
			}
		}
	}

	private boolean isSingleL2Loop(int taskA, int taskB) {
		for (int[] taskRow : log.getRows()) {
			boolean found = false;
			for (int i = 0; i < taskRow.length - 2; i++) {
				if (taskRow[i] != taskRow[i + 2])
					continue;
				if (!(taskRow[i] == taskA && taskRow[i + 1] == taskB)
						|| (taskRow[i] == taskB && taskRow[i + 1] == taskA))
					continue;
				if (found == true)
					return false;
				found = true;
			}
		}
		return true;
	}
	*/

	private Set<BitSet> selectPatterns(int sjTask, Map<BitSet, Integer> pattern2Count, List<Integer> connectedTasks) {
		Set<BitSet> chosenPatterns = new HashSet<BitSet>();

		Map<BitSet, Double> pattern2Frequency = new HashMap<BitSet, Double>();
		double frequencyTotal = 0D;
		for (Entry<BitSet, Integer> patternEntry : pattern2Count.entrySet()) {
			double frequency = calculatePatternFrequency(sjTask, patternEntry.getValue());
			pattern2Frequency.put(patternEntry.getKey(), frequency);
			frequencyTotal += frequency;
		}

		if (pattern2Count.size() > 0) {
			double nrPatterns = pattern2Count.size();
			double thresholdBase = frequencyTotal / nrPatterns;
			double thresholdMin = settings.patternThreshold < 0D ? (settings.patternThreshold * thresholdBase) : 0D;
			double thresholdPlus = settings.patternThreshold > 0D ? (settings.patternThreshold * (1D - thresholdBase)) : 0D;
			double threshold = thresholdBase + thresholdMin + thresholdPlus;

			for (Entry<BitSet, Double> patternEntry : pattern2Frequency.entrySet())
				if (patternEntry.getValue() >= threshold)
					chosenPatterns.add(patternEntry.getKey());
		}

		if (settings.danglingPatternStrategy != MinerSettings.DANGLING_PATTERN_IGNORE)
			for (int uncovered : BitpatternUtils.getUncoveredTasksByPatterns(chosenPatterns, connectedTasks)) {
				if (settings.danglingPatternStrategy == MinerSettings.DANGLING_PATTERN_ADD_XOR) {
					BitSet coveringPattern = BitpatternUtils.getPatternForSingleTask(uncovered, connectedTasks);
					chosenPatterns.add(coveringPattern);
				} else if (settings.danglingPatternStrategy == MinerSettings.DANGLING_PATTERN_ADD_AND) {
					if (chosenPatterns.size() == 0)
						chosenPatterns.add(BitpatternUtils.getPatternForSingleTask(uncovered, connectedTasks));
					for (BitSet pattern : chosenPatterns) {
						pattern.set(connectedTasks.indexOf(uncovered));
					}
				}
			}

		return chosenPatterns;
	}

	private double calculatePatternFrequency(int sjTask, int patternCount) {
		double originatorCount = metrics.getBasicRelations().getAMeasure(sjTask);
		double frequency = patternCount / originatorCount;
		return frequency;
	}

	private void acceptNextBestDependency(Set<Integer> possibleTailTasks, Set<Integer> possibleHeadTasks) {
		double bestDependency = -1D;
		boolean isL2L = false;
		int a = -1;
		int b = -1;
		for (int c : dependencyNet.getTasks()) {
			for (int d : possibleTailTasks) {
				double measure = metrics.getDependencyMeasure(c, d);
				double measure2 = settings.useOnlyNormalDependenciesForConnecting ? -1 : metrics.getL2lMeasure(c, d);
				if (dependencyNet.getStartTask() == d || dependencyNet.getEndTask() == c || c == d
						|| dependencyNet.isArc(c, d) || ((measure < bestDependency) && (measure2 < bestDependency)))
					continue;
				bestDependency = Math.max(measure2, measure);
				a = c;
				b = d;
				isL2L = measure2 > measure;
			}
			for (int d : possibleHeadTasks) {
				double measure = metrics.getDependencyMeasure(d, c);
				double measure2 = settings.useOnlyNormalDependenciesForConnecting ? -1 : metrics.getL2lMeasure(d, c);
				if (dependencyNet.getEndTask() == d || dependencyNet.getStartTask() == c || c == d
						|| dependencyNet.isArc(d, c) || ((measure < bestDependency) && (measure2 < bestDependency)))
					continue;
				bestDependency = Math.max(measure2, measure);
				a = d;
				b = c;
				isL2L = measure2 > measure;
			}
		}
		if (a != -1 && b != -1) {
			dependencyNet.setArc(a, b);
			if (isL2L)
				dependencyNet.setArc(b, a);
		}
	}

	private Set<Integer> getIncomingLongDistanceTasks(int task, Map<Integer, Set<Integer>> longDistanceDependencies) {
		Set<Integer> ldTasks = new HashSet<Integer>();
		for (Entry<Integer, Set<Integer>> e : longDistanceDependencies.entrySet()) {
			if (e.getValue().contains(task))
				ldTasks.add(e.getKey());
		}
		return ldTasks;
	}

	private Set<Integer> getOutgoingLongDistanceTasks(int task, Map<Integer, Set<Integer>> longDistanceDependencies) {
		Set<Integer> ldTasks = new HashSet<Integer>();
		ldTasks.addAll(longDistanceDependencies.get(task));
		return ldTasks;
	}
}
