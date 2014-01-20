package org.processmining.plugins.compliance.temporal;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.connections.impl.AbstractStrongReferencingConnection;

public class EnrichLog_Connection extends AbstractStrongReferencingConnection {
	public final static String ORIGINAL_LOG = "Original Log";
	public final static String ENRICHED_LOG = "Enriched Log";
	
	public EnrichLog_Connection(String label, XLog log, XLog enrichedlog) {		
		super(label);
		put(ORIGINAL_LOG, log);
		put(ENRICHED_LOG, enrichedlog);
	}
}
