package org.processmining.analysis.epcmerge;

import org.processmining.analysis.conformance.ConformanceAnalysisConfiguration;
import org.processmining.analysis.conformance.ConformanceAnalysisSettings;
import org.processmining.framework.models.epcpack.ConfigurableEPC;

public class EPCMergeExecutionThread extends Thread {
	private EPCMergeMethod myMethod;
	private EPCMergeSettings myNotificationTarget;

	/**
	 * Creates this thread and initializes its attributes.
	 * 
	 * @param method
	 *            the analysis method to be executed in this thread
	 * @param settings
	 *            the settings frame which has to be notified after the work is
	 *            done
	 */
	public EPCMergeExecutionThread(EPCMergeMethod method,
			EPCMergeSettings target) {
		myMethod = method;
		myNotificationTarget = target;
	}

	/**
	 * Executes the given analysis method, provides all registered categores
	 * with the results, and informs the settings frame that it is done.
	 * 
	 * @see ConformanceAnalysisSettings#threadDone threadDone
	 * @see ConformanceAnalysisConfiguration#hasRegisteredFor hasRegisteredFor
	 */
	public void run() {
		// invoke analysis method
		ConfigurableEPC currentResult = myMethod.analyse();
		// get those categories that registered for this result
		// notify settings frame
		myNotificationTarget.threadDone(currentResult);
	}

}
