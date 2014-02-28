package org.processmining.plugins.compliance.ui;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.fraud.model.fraud;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.connections.petrinets.PNRepResultConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;

public class CheckConformanceValue {
	
	PetrinetGraph net = null;
	XLog log = null;
	
	List<fraud> fr = new ArrayList<fraud>();
	public List<fraud> visualization(PluginContext context, PNRepResult result)
	{
		try {
			PNRepResultAllRequiredParamConnection conn = context.getConnectionManager().getFirstConnection(PNRepResultAllRequiredParamConnection.class, context, result);
			
			net = conn.getObjectWithRole(PNRepResultConnection.PN);
			log = conn.getObjectWithRole(PNRepResultConnection.LOG);
		} catch (Exception exc){
			context.log("No net can be found for this log replay result");
			
		} 
		
		DetectedSkippedEvents DSE = new DetectedSkippedEvents();
		
		return fr = DSE.detect_skip(net, log, result, context.getProgress());
	}
}
