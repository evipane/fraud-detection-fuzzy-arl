package org.processmining.analysis.performance.basicperformance.model.task;

import java.util.ArrayList;
import java.util.Arrays;

import org.processmining.analysis.performance.basicperformance.model.AbstractPerformance;
import org.processmining.framework.log.LogReader;

public class TaskTPerformance extends AbstractPerformance {

	public TaskTPerformance(LogReader inputLog) {
		super("Task", "Task performance", new ArrayList(Arrays.asList(inputLog
				.getLogSummary().getModelElements())));
	}

	public static String getNameCode() {
		return "Task";
	}
}
