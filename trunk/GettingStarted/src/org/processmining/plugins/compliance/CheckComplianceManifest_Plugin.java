/**
 * 
 */
package org.processmining.plugins.compliance;

import java.util.ArrayList;
import java.util.Collection;

import nl.tue.astar.AStarException;

import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.connections.ConnectionManager;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.util.ui.wizard.ListWizard;
import org.processmining.framework.util.ui.wizard.ProMWizardDisplay;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.ChooseAlgorithmStep;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.algorithms.IPNManifestReplayAlgorithm;
import org.processmining.plugins.petrinet.manifestreplayresult.Manifest;

/**
 * @author aadrians Feb 13, 2012
 * 
 */
//@Plugin(name = "Check Compliance Using Manifest Replayer",
//	returnLabels = { "Manifest" },
//	returnTypes = { Manifest.class },
//	parameterLabels = {"Petri net", "Event Log", "Algorithm", "Parameters" }, 
//	help = "Replay an event log on Petri net to get all manifest of patterns.", userAccessible = true)
public class CheckComplianceManifest_Plugin {
//	@UITopiaVariant(
//			affiliation = UITopiaVariant.EHV,
//			author = "Arya Adriansyah, Dirk Fahland",
//			email = "a.adriansyah@tue.nl, d.fahland@tue.nl",
//			pack = "Compliance")
//	@PluginVariant(variantLabel = "Check Compliance Using Conformance Checking", requiredParameterLabels = { 0, 1 })
	public Manifest replayLog(UIPluginContext context, PetrinetGraph net, XLog log) {
		/**
		 * Utilities
		 */
		// generate create pattern GUI
		XEventClassifier[] availableClassifiers = new XEventClassifier[4];
		availableClassifiers[0] = XLogInfoImpl.STANDARD_CLASSIFIER;
		availableClassifiers[1] = XLogInfoImpl.NAME_CLASSIFIER;
		availableClassifiers[2] = XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER;
		availableClassifiers[3] = XLogInfoImpl.RESOURCE_CLASSIFIER;

		// results, required earlier for wizard
		PNManifestReplayerParameter parameter = new PNManifestReplayerParameter();

		// generate pattern mapping GUI
		CheckCompliance_MapEvPattern2Trans_Smart_Step mapPatternStep = new CheckCompliance_MapEvPattern2Trans_Smart_Step(net, log, availableClassifiers);

		// generate algorithm selection GUI, look for initial marking and final markings
		Marking initialMarking;
		ConnectionManager connManager = context.getConnectionManager();
		try {
			InitialMarkingConnection iMarkingConn = connManager.getFirstConnection(InitialMarkingConnection.class,
					context, net);
			if (iMarkingConn != null) {
				initialMarking = iMarkingConn.getObjectWithRole(InitialMarkingConnection.MARKING);
			} else {
				initialMarking = new Marking();
			}
		} catch (Exception exc) {
			initialMarking = new Marking();
		}

		Marking[] finalMarkings;
		try {
			Collection<FinalMarkingConnection> conns = connManager.getConnections(FinalMarkingConnection.class,
					context, net);
			finalMarkings = new Marking[conns.size()];
			if (conns != null) {
				int i = 0;
				for (FinalMarkingConnection fmConn : conns) {
					finalMarkings[i] = fmConn.getObjectWithRole(FinalMarkingConnection.MARKING);
					i++;
				}
			}
		} catch (Exception exc) {
			finalMarkings = new Marking[0];
		}
		ChooseAlgorithmStep chooseAlgorithmStep = new ChooseAlgorithmStep(net, log, initialMarking, finalMarkings);

		// generate cost setting GUI
		MapCostStep_Smart mapCostStep = new MapCostStep_Smart(
				mapPatternStep.getEventClasses(),
				mapPatternStep.getTransitionClasses());

		// construct dialog wizard
		ArrayList<ProMWizardStep<PNManifestReplayerParameter>> listSteps = new ArrayList<ProMWizardStep<PNManifestReplayerParameter>>(
				4);
		listSteps.add(mapPatternStep);
		listSteps.add(chooseAlgorithmStep);
		listSteps.add(mapCostStep);

		ListWizard<PNManifestReplayerParameter> wizard = new ListWizard<PNManifestReplayerParameter>(listSteps);

		// show wizard
		parameter = ProMWizardDisplay.show(context, wizard, parameter);

		if (parameter == null) {
			context.getFutureResult(0).cancel(true);
			return null;
		}

		// show message: GUI mode
		parameter.setGUIMode(true);

		IPNManifestReplayAlgorithm alg = chooseAlgorithmStep.getSelectedAlgorithm();
		Manifest manifest = null;
		try {
			manifest = alg.replayLog(context, net, log, parameter);
		} catch (AStarException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return manifest;
	}

	public Manifest replayLog(PluginContext context, PetrinetGraph net, XLog log,
			IPNManifestReplayAlgorithm selectedAlg, PNManifestReplayerParameter parameters) {
		// checking preconditions here
		if (selectedAlg.isAllReqSatisfied(net, log, parameters)) {
			Manifest manifest = null;
			try {
				manifest = selectedAlg.replayLog(context, net, log, parameters);
			} catch (AStarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return manifest;
		} else {
			throw new IllegalArgumentException();
		}
	}
}
