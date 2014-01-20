package org.processmining.plugins.compliance;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractStrongReferencingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;

public class AbstractLogForCompliance_Connection extends AbstractStrongReferencingConnection {
	public final static String PETRINETGRAPH = "PetrinetGraph";
	public final static String ORIGINAL_LOG = "Original Log";
	public final static String ABSTRACTED_LOG = "Abstracted Log";
	
	public AbstractLogForCompliance_Connection(String label, XLog originalLog, PetrinetGraph net, XLog abstractedLog) {		
		super(label);
		put(PETRINETGRAPH, net);
		put(ORIGINAL_LOG, originalLog);
		put(ABSTRACTED_LOG, abstractedLog);
	}
}
