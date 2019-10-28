package org.processmining.plugins.bpmnminer.types;

import java.util.Arrays;

public class SequenceFragment {
	protected String middle;
	protected String[] before;
	protected String[] after;
	
	public SequenceFragment(int b, int a) {
		before = new String[b];
		after = new String[a];
	}
	
	public SequenceFragment(String m, int b, int a) {
		this(b, a);
		middle = m;
	}
	
	public SequenceFragment(String m, String[] b, String[] a) {
		middle = m;
		setBeforeContext(b);
		setAfterContext(a);
	}
	
	public String getEventId() {
		return middle;
	}
	
	public void setBeforeContext(String[] c) {
		before = c;
	}
	
	public void setAfterContext(String[] c) {
		after = c;
	}

	public boolean isSameMiddleAs(SequenceFragment other) {
		return middle.equals(other.middle);
	}
	
	public boolean isSameBeforeAs(SequenceFragment other) {
		return Arrays.equals(before, other.before);
	}
	
	public boolean isSameAfterAs(SequenceFragment other) {
		return Arrays.equals(after, other.after);
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(after);
		result = prime * result + Arrays.hashCode(before);
		result = prime * result + ((middle == null) ? 0 : middle.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SequenceFragment other = (SequenceFragment) obj;
		if (!Arrays.equals(after, other.after))
			return false;
		if (!Arrays.equals(before, other.before))
			return false;
		if (middle == null) {
			if (other.middle != null)
				return false;
		} else if (!middle.equals(other.middle))
			return false;
		return true;
	}

	public String toString() {
		return "SequenceFragment [middle=" + middle 
				+ ", before=" + Arrays.toString(before) 
				+ ", after=" + Arrays.toString(after) + "]";
	}
	
	
}
