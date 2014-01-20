/**
 * 
 */
package org.processmining.plugins.compliance.ui;

import java.util.Collection;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParamProvider;
import org.processmining.plugins.petrinet.replayer.ui.PNAlgorithmStep;

/**
 * @author arya
 * 
 */
public class PNReplayerUI {
	public static final int MAPPING = 0;
	public static final int ALGORITHM = 1;
	public static final int PARAMETERS = 2;

	// steps
	private int currentStep;
	private int nofSteps = 2; // algorithm and parameter

	// gui for each steps
	private JComponent[] replaySteps;
	
	// reference
	private IPNReplayParamProvider paramProvider = null;

	public Object[] getConfiguration(UIPluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping) {
		// provide warning if not all events are mapped
		checkCorrectMapping(mapping, log);
		
		replaySteps = new JComponent[nofSteps];
		replaySteps[0] = new PNAlgorithmStep(context, net, log, mapping);

		// set current step
		currentStep = 0;

		return showConfiguration(context, log, net, mapping);
	}
	
	private Object[] showConfiguration(UIPluginContext context, XLog log, PetrinetGraph net,
			TransEvClassMapping mapping) {
		// init result variable
		InteractionResult result = InteractionResult.NEXT;

		// configure interaction with user
		while (true) {
			if (currentStep < 0) {
				currentStep = 0;
			}
			if (currentStep >= nofSteps) {
				currentStep = nofSteps - 1;
			}

			result = context.showWizard("Replay in Petri net", currentStep == 0, currentStep == nofSteps - 1,
					replaySteps[currentStep]);
			switch (result) {
				case NEXT :
					go(1, context, net, log, mapping);
					break;
				case PREV :
					go(-1, context, net, log, mapping);
					break;
				case FINISHED :
					return new Object[] { mapping, ((PNAlgorithmStep) replaySteps[0]).getAlgorithm(), paramProvider.constructReplayParameter(replaySteps[1]) };
				default :
					return new Object[] { null };
			}
		}
	}
	

	/**
	 * go to next step in replaying. petri net and connection to log can be used
	 * as necessary by the implemented GUI
	 * 
	 * @param direction
	 * @param isTestingMode
	 * @param net
	 * @param conn
	 * @return
	 */
	private int go(int direction, UIPluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping) {
		currentStep += direction;

		// check which algorithm is selected and adjust parameter as necessary
		if (currentStep == 1) {
			this.paramProvider = ((PNAlgorithmStep) replaySteps[0]).getAlgorithm().constructParamProvider(context, net, log, mapping);
			replaySteps[1] = paramProvider.constructUI();
		}

		if ((currentStep >= 0) && (currentStep < nofSteps)) {
			return currentStep;
		}
		return 0;
	}

	/**
	 * Check if all event classes in the log has been mapped to at least a
	 * transition
	 * 
	 * @param conn
	 * @param log
	 * @return
	 */
	private boolean checkCorrectMapping(TransEvClassMapping mapping, XLog log) {
		
		XEventClassifier classifier = mapping.getEventClassifier();

		XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		XEventClasses eventClasses = summary.getEventClasses();
		Collection<XEventClass> colEventClasses = eventClasses.getClasses();
		
		// get all existing event classes which are unmapped
		colEventClasses.removeAll(mapping.values());

		if (!colEventClasses.isEmpty()) {
			// not all event class in the log is mapped
			String message = "";
			String limiter = "";
			for (XEventClass evClass : colEventClasses) {
				message += limiter;
				message += evClass.toString();
				limiter = "<br />- ";
			}

			JOptionPane.showMessageDialog(null, "<html>The following event class(es): <br/> - " + message
					+ "<br/> is (are) not mapped to any nodes.</html>", "Information", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}
		return true;
	}
}
