package org.processmining.plugins.bpmnminer.types;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.info.impl.XLogInfoImpl;

public class MinerSettings {
	public final static int DANGLING_PATTERN_ADD_XOR = 1;
	public final static int DANGLING_PATTERN_ADD_AND = 2;
	public final static int DANGLING_PATTERN_IGNORE = 3;
	
	public XEventClassifier classifier = XLogInfoImpl.STANDARD_CLASSIFIER;
	
	public double dependencyThreshold = 0.90;
	public double l1lThreshold = 0.90;
	public double l2lThreshold = 0.90;
	public double longDistanceThreshold = 0.90;
	public int dependencyDivisor = 1;
	public double causalityStrength = 0.80;
	public double duplicateThreshold = 0.10;

	public double patternThreshold = 0D;
	
	public boolean useAllConnectedHeuristics = true;
	public boolean useOnlyNormalDependenciesForConnecting = false;
	public boolean useLongDistanceDependency = false;
	public boolean useUniqueStartEndTasks = false;

	public boolean collapseL1l = true;
	public boolean preferAndToL2l = false;
	public boolean preventL2lWithL1l = true;

	public int backwardContextSize = 0;
	public int forwardContextSize = 0;
	
	public boolean suppressFitnessReport = true;
	
	public int danglingPatternStrategy = DANGLING_PATTERN_ADD_XOR;
	
	public String organizationalField = XOrganizationalExtension.KEY_RESOURCE;
	
}
