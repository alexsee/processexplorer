package org.processmining.plugins.bpmnminer;

/**
 * @author Alexander Seeliger on 28.10.2019.
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XLogImpl;

public class GroupedXLog implements Cloneable {
    private XEventClassifier classifier;
    private List<List<XTrace>> groups;
    private XAttributeMap attributeMap;
    private int nbTraces;
    private String name;
    private Map<String, Integer> trace2Index;

    private GroupedXLog(XEventClassifier c) {
        this.nbTraces = 0;
        this.classifier = c;
    }

    public GroupedXLog(XLog log) {
        this(log, XLogInfoImpl.STANDARD_CLASSIFIER, true);
    }

    public GroupedXLog(XLog log, XEventClassifier c, boolean useHashMap) {
        this.nbTraces = 0;
        this.trace2Index = new HashMap();
        this.classifier = c;
        this.attributeMap = log.getAttributes();
        this.groups = new ArrayList();
        XAttribute nameAttribute = (XAttribute)this.attributeMap.get("concept:name");
        this.name = nameAttribute == null ? "---" : nameAttribute.toString();
        int length = this.name.lastIndexOf(46);
        if (length >= 0) {
            if (this.name.subSequence(length, this.name.length()).equals(".gz")) {
                length = this.name.lastIndexOf(46, length - 1);
            }

            if (length != 0) {
                this.name = this.name.substring(0, length);
            }
        }

        int index;
        XTrace trace;
        for(Iterator var9 = log.iterator(); var9.hasNext(); ((List)this.groups.get(index)).add(trace)) {
            trace = (XTrace)var9.next();
            ++this.nbTraces;
            index = -1;
            if (useHashMap) {
                String traceHash = this.traceHash(trace);
                if (this.trace2Index.containsKey(traceHash)) {
                    index = (Integer)this.trace2Index.get(traceHash);
                }
            } else {
                for(int i = 0; i < this.groups.size(); ++i) {
                    if (this.tracesSimilar(trace, (XTrace)((List)this.groups.get(i)).get(0))) {
                        index = i;
                        break;
                    }
                }
            }

            if (index == -1) {
                index = this.groups.size();
                this.groups.add(new ArrayList());
                this.trace2Index.put(this.traceHash(trace), index);
            }
        }

        Collections.sort(this.groups, new Comparator<List<XTrace>>() {
            public int compare(List<XTrace> a, List<XTrace> b) {
                return b.size() - a.size();
            }
        });
    }

    public Object clone() {
        GroupedXLog output = new GroupedXLog(this.classifier);
        output.groups = new ArrayList();

        for(int i = 0; i < this.groups.size(); ++i) {
            XTrace[] sub_array = (XTrace[])((List)this.groups.get(i)).toArray(new XTrace[0]);
            output.groups.add(new ArrayList());

            for(int j = 0; j < sub_array.length; ++j) {
                ((List)output.groups.get(i)).add(sub_array[j]);
            }
        }

        output.attributeMap = (XAttributeMap)this.attributeMap.clone();
        output.nbTraces = this.nbTraces;
        output.name = this.name;
        return output;
    }

    public List<XTrace> get(int index) {
        return (List)this.groups.get(index);
    }

    public XAttributeMap getAttributeMap() {
        return this.attributeMap;
    }

    public XLog getLog() {
        XLogImpl log = new XLogImpl(this.attributeMap);

        for(int i = 0; i < this.groups.size(); ++i) {
            log.addAll((Collection)this.groups.get(i));
        }

        return log;
    }

    public XLog getLog(Collection<Integer> indices) {
        XLogImpl log = new XLogImpl(this.attributeMap);
        Iterator var4 = indices.iterator();

        while(var4.hasNext()) {
            Integer i = (Integer)var4.next();
            log.addAll((Collection)this.groups.get(i));
        }

        return log;
    }

    public XLog getLog(int... indices) {
        XLogImpl log = new XLogImpl(this.attributeMap);

        for(int i = 0; i < indices.length; ++i) {
            log.addAll((Collection)this.groups.get(indices[i]));
        }

        return log;
    }

    public XLog getGroupedLog() {
        XLogImpl log = new XLogImpl(this.attributeMap);

        for(int i = 0; i < this.groups.size(); ++i) {
            log.add((XTrace)((List)this.groups.get(i)).get(0));
        }

        return log;
    }

    public String getName() {
        return this.name;
    }

    public int getNbTraces() {
        return this.nbTraces;
    }

    public void remove(int index) {
        this.nbTraces -= ((List)this.groups.get(index)).size();
        this.groups.remove(index);
    }

    public void remove(XTrace trace) throws IllegalArgumentException {
        for(int i = 0; i < this.groups.size(); ++i) {
            if (this.tracesSimilar(trace, (XTrace)((List)this.groups.get(i)).get(0))) {
                this.nbTraces -= ((List)this.groups.get(i)).size();
                this.groups.remove(i);
                return;
            }
        }

        throw new IllegalArgumentException("No similar group found");
    }

    public int size() {
        return this.groups.size();
    }

    private boolean tracesSimilar(XTrace one, XTrace two) {
        return tracesSimilar(one, two, this.classifier);
    }

    public static boolean tracesSimilar(XTrace one, XTrace two, XEventClassifier c) {
        if (one.size() != two.size()) {
            return false;
        } else {
            for(int i = 1; i < one.size(); ++i) {
                if (!c.sameEventClass((XEvent)one.get(i), (XEvent)two.get(i))) {
                    return false;
                }
            }

            return c.sameEventClass((XEvent)one.get(0), (XEvent)two.get(0));
        }
    }

    public String traceHash(XTrace trace) {
        String th = "";

        XEvent e;
        for(Iterator var4 = trace.iterator(); var4.hasNext(); th = th + this.classifier.getClassIdentity(e) + "+++++") {
            e = (XEvent)var4.next();
        }

        return th;
    }
}

