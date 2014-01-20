/**
 * 
 */
package org.processmining.plugins.compliance;

import java.util.Collection;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.TransClass2PatternMap;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClass;

/**
 * @author aadrians
 * Feb 26, 2012
 *
 */
public class CheckCompliance_MapEvPattern2Trans_Smart_Step implements ProMWizardStep<PNManifestReplayerParameter>{
	private PetrinetGraph net;
	private XLog log;
	
	private MapEvPattern2Trans_Smart_UI ui;
	
	public CheckCompliance_MapEvPattern2Trans_Smart_Step(PetrinetGraph net, XLog log, XEventClassifier[] availableClassifier) {
		this.ui = new MapEvPattern2Trans_Smart_UI(log, net, availableClassifier);
		this.net = net;
		this.log = log;
	}

	public String getTitle() {
		return "Map Transition Classes to Patterns";
	}

	public JComponent getComponent(PNManifestReplayerParameter model) {
		//ui.initiateTransClass(net, ui.getSelectedClassifier(), ui.getEvClassPatternArr());
		return ui;
	}

	public PNManifestReplayerParameter apply(PNManifestReplayerParameter model, JComponent component) {
		// the mapping needs to be stored
		TransClass2PatternMap mapping = ui.getPatternMap();
		model.setMapping(mapping);
		return model;
	}
	
	public Collection<TransClass> getTransitionClasses() {
		return ui.getTransitionClasses().getTransClasses();
	}
	
	public Collection<XEventClass> getEventClasses() {
		return ui.getEventClasses().getClasses();
	}


	public boolean canApply(PNManifestReplayerParameter model, JComponent component) {
		return true;
	}

}
