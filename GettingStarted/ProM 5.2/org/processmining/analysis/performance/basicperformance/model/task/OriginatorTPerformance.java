package org.processmining.analysis.performance.basicperformance.model.task;

import java.util.ArrayList;
import java.util.Arrays;

import org.processmining.analysis.performance.basicperformance.model.AbstractPerformance;
import org.processmining.framework.log.LogReader;

public class OriginatorTPerformance extends AbstractPerformance {
	public OriginatorTPerformance(LogReader inputLog) {
		super("Originator", "Originator performance", new ArrayList(Arrays
				.asList(inputLog.getLogSummary().getOriginators())));
	}

	public static String getNameCode() {
		return "Originator";
	}
}
