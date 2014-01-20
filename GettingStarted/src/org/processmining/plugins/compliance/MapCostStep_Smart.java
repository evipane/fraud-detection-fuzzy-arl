/**
 * 
 */
package org.processmining.plugins.compliance;

import java.util.Collection;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.ui.wizard.ProMWizardStep;
import org.processmining.plugins.astar.petrinet.manifestreplay.ui.ClassCostMapPanel;
import org.processmining.plugins.petrinet.manifestreplayer.PNManifestReplayerParameter;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClass;

/**
 * @author aadrians
 * Feb 26, 2012
 *
 */
public class MapCostStep_Smart implements ProMWizardStep<PNManifestReplayerParameter>{
	private ClassCostMapPanel costBasedCompleteGUI;
	
	private Collection<XEventClass> eventClasses;
	private Collection<TransClass> transitionClasses;
	
	public MapCostStep_Smart(Collection<XEventClass> eventClasses, Collection<TransClass> transitionClasses){
		this.eventClasses = eventClasses;
		this.transitionClasses = transitionClasses;
	}
	
	public String getTitle() {
		return "Set Cost for Deviation";
	}

	public JComponent getComponent(PNManifestReplayerParameter model) {
		this.costBasedCompleteGUI = new ClassCostMapPanel(transitionClasses, eventClasses);
		return costBasedCompleteGUI;
	}

	public PNManifestReplayerParameter apply(PNManifestReplayerParameter model, JComponent component) {
		model.setMapEvClass2Cost(costBasedCompleteGUI.getMapEvClassToCost());
		model.setTrans2Cost(costBasedCompleteGUI.getMapTransClassToCost());
		model.setMaxNumOfStates(costBasedCompleteGUI.getMaxNumOfStates());
		return model;
	}

	public boolean canApply(PNManifestReplayerParameter model, JComponent component) {
		return true;
	}

}
