package org.processmining.plugins.bpmnminer.types;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.plugins.bpmnminer.GroupedXLog;
import org.processmining.plugins.bpmnminer.causalnet.CausalNet;

import java.util.*;
import java.util.Map.Entry;

public class EventLogTaskMapper {
    public static String UNIQUE_START_LABEL = "__SOURCE__";
    public static String UNIQUE_END_LABEL = "__SINK__";

    protected XLog log;
    protected GroupedXLog groupedLog;
    protected XEventClassifier classifier;

    protected Map<SequenceFragment, Integer> mapping;
    protected Map<SequenceFragment, Integer> fragment2count;

    protected int backSize, forwSize;
    protected boolean artificialStartEnd;
    protected boolean collapseRepeats;
    protected double duplicateThreshold;

    public static EventLogTaskMapper createMapping(CausalNet n, XEventClassifier c) {
        EventLogTaskMapper mapping = new EventLogTaskMapper(c);
        for (int t : n.getTasks()) {
            mapping.setTaskMapping(t, n.getLabel(t), new String[]{"" + t}, new String[]{"" + t});
        }
        return mapping;
    }

    public static EventLogTaskMapper createMapping(CausalNet n, XLog l, XEventClassifier c) {
        EventLogTaskMapper mapping = new EventLogTaskMapper(l, c);
        setupMapping(mapping, n, XEventClasses.deriveEventClasses(c, l));
        return mapping;
    }

    public static EventLogTaskMapper createMapping(CausalNet n, XEventClasses cl) {
        EventLogTaskMapper mapping = new EventLogTaskMapper(cl.getClassifier());
        setupMapping(mapping, n, cl);
        return mapping;
    }

    private static void setupMapping(EventLogTaskMapper mapping, CausalNet n, XEventClasses cl) {
        for (int t : n.getTasks()) {
            String labelTask = n.getLabel(t);
            String labelTaskClean = labelTask.replace("+complete", "").replace("\\n", "").replace("\n", "");
            for (XEventClass eventClass : cl.getClasses()) {
                String labelEvent = eventClass.getId();
                String labelEventClean = labelEvent.replace("+complete", "").replace("\\n", "").replace("\n", "");
                if (labelTaskClean.equals(labelEventClean)) {
                    mapping.setTaskMapping(t, labelEvent, new String[]{"" + t}, new String[]{"" + t});
                }
            }
            if (labelTaskClean.equals(UNIQUE_START_LABEL))
                mapping.setTaskMapping(t, UNIQUE_START_LABEL, new String[]{"" + t}, new String[]{"" + t});
            if (labelTaskClean.equals(UNIQUE_END_LABEL))
                mapping.setTaskMapping(t, UNIQUE_END_LABEL, new String[]{"" + t}, new String[]{"" + t});
        }
    }

    public static EventLogTaskMapper createMapping(XLog l, XEventClassifier c,
                                                   int backContext, int forwContext, boolean aStartEnd, boolean collapse, double dThres) {
        EventLogTaskMapper mapping = new EventLogTaskMapper(l, c);
        mapping.setup(backContext,
                forwContext,
                aStartEnd,
                collapse,
                dThres);
        return mapping;
    }

    public EventLogTaskMapper(XEventClassifier c) {
        log = null;
        groupedLog = null;
        classifier = c;
        clear();
    }

    public EventLogTaskMapper(XLog l, XEventClassifier c) {
        log = l;
        groupedLog = new GroupedXLog(l, c, true);
        classifier = c;
        clear();
    }

    public String toString() {
        String repr = "";
        repr += "EventLogTaskMapper: " + backSize + ", " + forwSize + " | " + artificialStartEnd + " | " + collapseRepeats + " | " + duplicateThreshold;
        repr += "\r\n";
        for (Entry<SequenceFragment, Integer> e : mapping.entrySet()) {
            repr += " " + e.getKey().getEventId() + " --> " + e.getValue();
            repr += "\r\n";
        }
        return repr;
    }

    public void clear() {
        mapping = new HashMap<SequenceFragment, Integer>();
        fragment2count = new HashMap<SequenceFragment, Integer>();

        backSize = 0;
        forwSize = 0;
        artificialStartEnd = false;
        collapseRepeats = false;
        duplicateThreshold = 0D;
    }

    public void setup(int backContext, int forwContext, boolean aStartEnd, boolean collapse, double dThres) {
        clear();

        backSize = backContext;
        forwSize = forwContext;
        artificialStartEnd = aStartEnd;
        collapseRepeats = collapse;
        duplicateThreshold = dThres;

        makeFullMapping();
        collapseMapping();
        filterMapping();
    }

    public boolean hasTaskMapping(SequenceFragment fragment) {
        return mapping.containsKey(fragment);
    }

    public boolean hasTaskMapping(String eventId, String[] bContext, String[] fContext) {
        SequenceFragment fragment = new SequenceFragment(eventId, bContext, fContext);
        return hasTaskMapping(fragment);
    }

    public void setTaskMapping(int task, SequenceFragment fragment) {
        mapping.put(fragment, task);
        if (fragment.getEventId().equals(UNIQUE_START_LABEL) || fragment.getEventId().equals(UNIQUE_END_LABEL))
            this.artificialStartEnd = true;
    }

    public void setTaskMapping(int task, String eventId, String[] bContext, String[] fContext) {
        SequenceFragment fragment = new SequenceFragment(eventId, bContext, fContext);
        setTaskMapping(task, fragment);
    }

    public int getTaskMapping(SequenceFragment fragment) {
        return mapping.get(fragment);
    }

    public int getTaskMapping(String eventId, String[] bContext, String[] fContext) {
        SequenceFragment fragment = new SequenceFragment(eventId, bContext, fContext);
        return getTaskMapping(fragment);
    }

    public Set<XTrace> getMatchingTraces(int[] row) {
        Set<XTrace> traces = new HashSet<XTrace>();
        for (int group = 0; group < groupedLog.size(); group++) {
            XTrace trace = groupedLog.get(group).get(0);
            int trow[] = getIntegerTrace(trace);
            if (Arrays.equals(trow, row))
                traces.addAll(groupedLog.get(group));
        }
        return traces;
    }

    public String[] classifyTrace(XTrace trace) {
        List<String> sequence = new ArrayList<String>();

        if (artificialStartEnd)
            sequence.add("__SOURCE__");
        for (int j = 0; j < trace.size(); j++) {
            String clazz = classifier.getClassIdentity(trace.get(j));
            sequence.add(clazz);
        }
        if (artificialStartEnd)
            sequence.add("__SINK__");

        return sequence.toArray(new String[]{});
    }

    public Integer getIntegerFragment(String[] sequence, int position) {
        String eventId = sequence[position];
        String[] bContext = getBackwardContext(sequence, position, backSize);
        String[] fContext = getForwardContext(sequence, position, forwSize);
        SequenceFragment fragment = new SequenceFragment(eventId, bContext, fContext);
        Integer intcode = mapping.get(fragment);
        return intcode;
    }

    public Integer getIntegerFragment(XTrace trace, int position) {
        String[] sequence = classifyTrace(trace);
        return getIntegerFragment(sequence, position);
    }

    public Set<Integer> getIntegersEventClass(String eventId) {
        Set<Integer> usedInts = new HashSet<Integer>();
        for (Entry<SequenceFragment, Integer> entry : mapping.entrySet())
            if (entry.getKey().getEventId().equals(eventId))
                usedInts.add(entry.getValue());
        return usedInts;
    }

    public String getEventClassInteger(int task) {
        for (Entry<SequenceFragment, Integer> entry : mapping.entrySet())
            if (entry.getValue().equals(task))
                return entry.getKey().getEventId();
        return null;
    }

    public int[] getIntegerTrace(XTrace trace) {
        String[] sequence = classifyTrace(trace);
        List<Integer> row = new ArrayList<Integer>();

        String previousEventId = null;
        int previousIntcode = -1;
        for (int i = 0; i < sequence.length; i++) {
            String eventId = sequence[i];

            if (collapseRepeats && eventId.equals(previousEventId)) {
                row.add(previousIntcode);
                continue;
            }

            Integer intcode = getIntegerFragment(sequence, i);
            if (intcode == null) {
                Integer highestTask = getBestTaskIdForEventClass(eventId);
                intcode = (highestTask == null) ? null : highestTask;
            }
            if (intcode != null) {
                row.add(intcode);
                previousEventId = eventId;
                previousIntcode = intcode;
            } else {
                System.err.println(eventId + " could not be mapped");
            }
        }

        int[] r = new int[row.size()];
        for (int i = 0; i < r.length; i++)
            r[i] = row.get(i);

        return r;
    }

    public IntegerEventLog getIntegerLog(GroupedXLog groupedLog) {
        IntegerEventLog integerLog = new IntegerEventLog();
        for (int group = 0; group < groupedLog.size(); group++) {
            int[] row = addRowFromTrace(integerLog, groupedLog.get(group).get(0));
            integerLog.setRowCount(row, groupedLog.get(group).size());
        }
        return integerLog;
    }

    public IntegerEventLog getIntegerLog(XLog log) {
        IntegerEventLog integerLog = new IntegerEventLog();
        for (int tr = 0; tr < log.size(); tr++) {
            addRowFromTrace(integerLog, log.get(tr));
            // Row count is increased automatically
        }
        return integerLog;
    }

    public IntegerEventLog getIntegerLog() {
        return getIntegerLog(groupedLog);
    }

    public Set<Integer> getTasks() {
        return new HashSet<Integer>(mapping.values());
    }

    public XLog getLog() {
        return log;
    }

    public GroupedXLog getGroupedLog() {
        return groupedLog;
    }

    public XEventClassifier getClassifier() {
        return classifier;
    }

    private int[] addRowFromTrace(IntegerEventLog integerLog, XTrace trace) {
        int[] row = getIntegerTrace(trace);
        String[] sequence = classifyTrace(trace);
        for (int i = 0; i < row.length; i++)
            integerLog.setLabel(row[i], sequence[i]);
        integerLog.addRow(row);
        return row;
    }

    protected void makeFullMapping() {
        int nextcode = 0;

        for (int group = 0; group < groupedLog.size(); group++) {
            XTrace trace = groupedLog.get(group).get(0);
            String[] sequence = classifyTrace(trace);

            String previousEventId = null;
            for (int i = 0; i < sequence.length; i++) {
                String eventId = sequence[i];

                if (collapseRepeats && eventId.equals(previousEventId))
                    continue;

                String[] bContext = getBackwardContext(sequence, i, backSize);
                String[] fContext = getForwardContext(sequence, i, forwSize);
                SequenceFragment fragment = new SequenceFragment(eventId, bContext, fContext);

                if (!mapping.containsKey(fragment)) {
                    mapping.put(fragment, nextcode);
                    fragment2count.put(fragment, 1);
                    nextcode++;
                } else {
                    fragment2count.put(fragment, fragment2count.get(fragment) + groupedLog.get(group).size());
                }

                previousEventId = eventId;
            }
        }
    }

    protected void collapseMapping() {
        while (true) {
            boolean actionPerformed = false;
            for (SequenceFragment first : mapping.keySet()) {
                //if (actionPerformed)
                //	break;
                int firstKey = mapping.get(first);
                for (SequenceFragment second : mapping.keySet()) {
                    int secondKey = mapping.get(second);
                    if (firstKey == secondKey)
                        continue;
                    if (!first.isSameMiddleAs(second))
                        continue;
                    if (first.isSameBeforeAs(second) || first.isSameAfterAs(second)) {
                        actionPerformed = true;
                        for (SequenceFragment third : mapping.keySet()) {
                            if (mapping.get(third) == secondKey) {
                                mapping.put(third, firstKey);
                            }
                        }
                    }
                }
            }
            if (!actionPerformed)
                break;
        }
    }

    protected void filterMapping() {
        for (String eventId : getEventClasses()) {
            int totalFrequency = getEventClassFrequency(eventId);
            Integer highestTask = getBestTaskIdForEventClass(eventId);
            if (highestTask == null)
                continue;
            for (int t : getIntegersEventClass(eventId)) {
                int f = getTaskFrequency(t);
                double dupFrequency = (double) f / (double) totalFrequency;
                if (dupFrequency < duplicateThreshold) {
                    for (SequenceFragment sf : mapping.keySet()) {
                        if (mapping.get(sf) == t) {
                            mapping.put(sf, highestTask);
                        }
                    }
                }
            }
        }
    }

    protected Integer getBestTaskIdForEventClass(String eventId) {
        int highestFrequency = 0;
        Integer highestTask = null;
        for (int t : getIntegersEventClass(eventId)) {
            int f = getTaskFrequency(t);
            if (f > highestFrequency || highestTask == null) {
                highestFrequency = f;
                highestTask = t;
            }
        }
        return highestTask;
    }

    protected int getTaskFrequency(Integer task) {
        int count = 0;
        for (Entry<SequenceFragment, Integer> entry : mapping.entrySet())
            if (entry.getValue() == task)
                if (fragment2count.containsKey(entry.getKey()))
                    count += fragment2count.get(entry.getKey());
                else
                    count++;
        return count;
    }

    protected int getEventClassFrequency(String eventId) {
        int count = 0;
        for (Entry<SequenceFragment, Integer> entry : fragment2count.entrySet())
            if (entry.getKey().getEventId().equals(eventId))
                count += entry.getValue();
        return count;
    }

    protected Set<String> getEventClasses() {
        Set<String> eventIds = new HashSet<String>();
        for (Entry<SequenceFragment, Integer> entry : mapping.entrySet())
            eventIds.add(entry.getKey().getEventId());
        return eventIds;
    }

    protected String[] sliceContext(String[] sequence, int position, int size, int step) {
        List<String> context = new ArrayList<String>();
        String previousClazz = sequence[position];
        while (context.size() < Math.abs(size)) {
            position += step;
            if (position < 0 || position >= sequence.length) {
                context.add("");
                continue;
            }
            String clazz = sequence[position];
            if (collapseRepeats && clazz.equals(previousClazz))
                continue;
            context.add(clazz);
            previousClazz = clazz;
        }
        return context.toArray(new String[]{});
    }

    protected String[] getBackwardContext(String[] sequence, int position, int size) {
        return sliceContext(sequence, position, size, -1);
    }

    protected String[] getForwardContext(String[] sequence, int position, int size) {
        return sliceContext(sequence, position, size, 1);
    }
}
