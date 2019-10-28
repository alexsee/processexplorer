package org.processmining.plugins.bpmnminer.util;

import java.text.DecimalFormat;

public class StringUtils {
	public static String padDouble(double d, int length) {
	    DecimalFormat format = new DecimalFormat("#########.##");
	    String ds;
		if (d == (int) d)
			ds = Integer.toString((int) d);
		else
			ds =  format.format(d);
		
		return String.format("%"+length+"s", ds);
	}
}
