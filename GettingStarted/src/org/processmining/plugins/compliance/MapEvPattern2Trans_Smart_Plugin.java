/**
 * 
 */
package org.processmining.plugins.compliance;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.annotations.ConnectionObjectFactory;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.manifestreplayer.TransClass2PatternMap;

/**
 * @author aadrians
 *
 */
@ConnectionObjectFactory
@Plugin(name = "connect log events to Petri nets with omega transitions", 
	parameterLabels = { "Log", "Petrinet" },
	returnLabels = "mapping",
	returnTypes = TransClass2PatternMap.class,
	userAccessible = false)
public class MapEvPattern2Trans_Smart_Plugin {
	
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://www.processmining.org/",
			pack="Compliance")
	@PluginVariant(requiredParameterLabels = { 0, 1 })
	public TransClass2PatternMap connect(UIPluginContext context, XLog log, PetrinetGraph net) {

		// list possible classifiers
		XEventClassifier[] availableEventClass = new XEventClassifier[4];
		availableEventClass[0] = XLogInfoImpl.STANDARD_CLASSIFIER; 
		availableEventClass[1] = XLogInfoImpl.NAME_CLASSIFIER; 
		availableEventClass[2] = XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER; 
		availableEventClass[3] = XLogInfoImpl.RESOURCE_CLASSIFIER; 
		
		// build and show the UI to make the mapping
		MapEvPattern2Trans_Smart_UI ui = new MapEvPattern2Trans_Smart_UI(log, net, availableEventClass);
		InteractionResult result = context.showWizard("Mapping Petrinet - Event Class of Log", true, true, ui);

		// create the connection or not according to the button pressed in the UI
		EvClassLogPetrinetConnection con = null;
		if (result == InteractionResult.FINISHED) {
			return ui.getPatternMap();
		} else {
			return null;
		}
	}
}
