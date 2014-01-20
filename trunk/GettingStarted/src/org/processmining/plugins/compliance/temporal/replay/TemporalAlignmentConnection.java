package org.processmining.plugins.compliance.temporal.replay;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractStrongReferencingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.DataConformance.ResultReplay;

public class TemporalAlignmentConnection extends AbstractStrongReferencingConnection {
	public final static String PETRINETGRAPH = "PetrinetGraph";
	public final static String LOG = "Log";
	public final static String PNREPRESULT = "ReplayResult";
	
	public TemporalAlignmentConnection(String label, PetrinetGraph net, XLog log, ResultReplay alignment) {		
		super(label);
		put(PETRINETGRAPH, net);
		put(LOG, log);
		put(PNREPRESULT, alignment);
	}
}
