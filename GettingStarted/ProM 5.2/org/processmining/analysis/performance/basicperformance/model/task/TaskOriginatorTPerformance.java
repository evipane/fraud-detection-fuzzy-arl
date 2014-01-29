package org.processmining.analysis.performance.basicperformance.model.task;

import java.util.ArrayList;
import java.util.Arrays;

import org.processmining.analysis.performance.basicperformance.model.AbstractPerformance2D;
import org.processmining.framework.log.LogReader;

public class TaskOriginatorTPerformance extends AbstractPerformance2D {

	public TaskOriginatorTPerformance(LogReader inputLog) {
		super("Task-Originator", "Task", "Originator",
				"Task-Originator performance", new ArrayList<String>(Arrays
						.asList(inputLog.getLogSummary().getModelElements())),
				new ArrayList<String>(Arrays.asList(inputLog.getLogSummary()
						.getOriginators())));
	}

	public static String getNameCode() {
		return "Task-Originator";
	}
}
