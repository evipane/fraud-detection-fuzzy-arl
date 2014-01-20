package org.processmining.plugins.compliance.logprepare;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractStrongReferencingConnection;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

public class GetLogFromAlignment_Connection extends AbstractStrongReferencingConnection {
	public final static String PNREPRESULT = "Alignment";
	public final static String LOG = "Extracted Log";
	
	public GetLogFromAlignment_Connection(String label, PNRepResult alignment, XLog log) {		
		super(label);
		put(PNREPRESULT, alignment);
		put(LOG, log);
	}
}
