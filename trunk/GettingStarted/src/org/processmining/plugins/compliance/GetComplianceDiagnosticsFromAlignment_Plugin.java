/**
 * 
 */
package org.processmining.plugins.compliance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.PNRepResultAllRequiredParamConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * @author dfahland
 * 
 */
@Plugin(name = "Get Compliance Diagnostics From Alignment",
	returnLabels = { "Compliance Diagnostics" },
	returnTypes = { PNRepResult.class },
	parameterLabels = {"Petri net", "Event Log", "Mapping", "Replay Algorithm", "Parameters" }, 
	userAccessible = true
	)
public class GetComplianceDiagnosticsFromAlignment_Plugin {
	
	/**
	 * Main method to replay log.
	 * 
	 * @param context
	 * @param net
	 * @param log
	 * @param mapping
	 * @param selectedAlg
	 * @param parameters
	 * @return
	 */
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Get Compliance Diagnostics From Alignment", requiredParameterLabels = { 0 })
	public PNRepResult replayLog(PluginContext context, PNRepResult replayRes) {
		// retrieve connection of the replay result that yields the 
		// log and TransEvClassMapping used for replaying
		PNRepResultAllRequiredParamConnection c;
		try {
			c = context.getConnectionManager().getFirstConnection(PNRepResultAllRequiredParamConnection.class, context, replayRes);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "Could not find connection to log and model of the alignment.");
		}
		
		XLog log = c.getObjectWithRole(PNRepResultAllRequiredParamConnection.LOG);
		PetrinetGraph net = c.getObjectWithRole(PNRepResultAllRequiredParamConnection.PN);
		IPNReplayAlgorithm selectedAlg = c.getObjectWithRole(PNRepResultAllRequiredParamConnection.REPLAYALGORITHM);
		IPNReplayParameter parameters = c.getObjectWithRole(PNRepResultAllRequiredParamConnection.REPLAYPARAMETERS);
		TransEvClassMapping mapping = (TransEvClassMapping)c.getObjectWithRole(PNRepResultAllRequiredParamConnection.TRANS2EVCLASSMAPPING);
			
		final List<SyncReplayResult> adoptedAlignments = new ArrayList<SyncReplayResult>();
		// create traces in the aligned log (each trace is one trace class from the replay)
		for (SyncReplayResult alignment : replayRes) {
			List<SyncReplayResult> r2 = adoptResult(alignment, mapping);
			adoptedAlignments.addAll(r2);
		}
		PNRepResult adoptedResult = new PNRepResult(adoptedAlignments);
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String timeString = dateFormat.format(date);
		context.getFutureResult(0).setLabel("Compliance Diagnostics (created @"+timeString+")");
		
		context.addConnection(new PNRepResultAllRequiredParamConnection(
				"Connection between replay result, " + XConceptExtension.instance().extractName(log)
						+ ", and " + net.getLabel(), net, log, mapping, selectedAlg, parameters, adoptedResult));

			
		return adoptedResult;
	}
	
	protected List<SyncReplayResult> adoptResult(SyncReplayResult res, TransEvClassMapping mapping)
	{
		
		System.out.println("adopting result");
		
		List<StepTypes> stepTypes = new ArrayList<StepTypes>(res.getNodeInstance().size());
		List<Object> nodeInstance = new ArrayList<Object>();
		
		for (int step=0; step < res.getNodeInstance().size(); step++) {
			StepTypes type = res.getStepTypes().get(step);
			Object node = res.getNodeInstance().get(step);
			
			if (node instanceof XEventClass) {
				nodeInstance.add(node);
				if (AbstractLogForCompliance_Plugin.OMEGA_CLASS.equals(node) && type == StepTypes.LMGOOD) {
					// do not highlight synchronous moves of omega transitions
					stepTypes.add(StepTypes.MINVI);
				} else {
					stepTypes.add(type);	
				}

			} else if (node instanceof Transition) {
				XEventClass eClass = mapping.get(node);
				if (AbstractLogForCompliance_Plugin.DUMMY.equals(eClass)) {
					nodeInstance.add(node);
				} else if (AbstractLogForCompliance_Plugin.OMEGA_CLASS.equals(eClass)) { 
					nodeInstance.add(node);
				} else {
					nodeInstance.add(eClass);
				}
				
				if (AbstractLogForCompliance_Plugin.OMEGA_CLASS.equals(eClass) && type == StepTypes.LMGOOD) {
					// do not highlight synchronous moves of omega transitions
					stepTypes.add(StepTypes.MINVI);
				} else {
					stepTypes.add(type);	
				}
			} else if (node instanceof String) {

				nodeInstance.add(node);
				if (AbstractLogForCompliance_Plugin.OMEGA_CLASS.getId().equals(node) && type == StepTypes.LMGOOD) {
					// do not highlight synchronous moves of omega transitions
					stepTypes.add(StepTypes.MINVI);
				} else {
					stepTypes.add(type);
				}

			}
		}
		
		List<SyncReplayResult> allResults = new ArrayList<SyncReplayResult>();
		for (int traceIndex : res.getTraceIndex()) {
			SyncReplayResult result = new SyncReplayResult(nodeInstance, stepTypes, traceIndex);
			
			// copy additional information of each trace
			result.setReliable(res.isReliable());
			result.setInfo(res.getInfo());
			allResults.add(result);
		}

		return allResults;
	}

	protected static PNRepResult cancel(PluginContext context, String message) {
		System.out.println("[Generate Diagnostics Control-Flow Compliance]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
	
}
