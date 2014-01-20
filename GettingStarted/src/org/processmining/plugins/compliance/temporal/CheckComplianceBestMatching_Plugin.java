/**
 * 
 */
package org.processmining.plugins.compliance.temporal;

import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.providedobjects.ProvidedObjectID;
import org.processmining.models.connections.petrinets.PNMatchInstancesRepResultConnection;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinetwithdata.newImpl.PetriNetWithData;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.DataConformance.ResultReplay;
import org.processmining.plugins.compliance.AbstractLogForCompliance_Plugin;
import org.processmining.plugins.compliance.logprepare.GetLogFromBestMatchingAlignments_Plugin;
import org.processmining.plugins.compliance.temporal.replay.TemporalAlignment;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * @author aadrians Feb 13, 2012
 * 
 */
@Plugin(name = "Check Temporal Compliance Using Data-Aware Conformance (Match Instances)",
	returnLabels = { "Compliance Diagnostics", "Time-stamp enriched Log" },
	returnTypes = { ResultReplay.class, XLog.class },
	parameterLabels = {"All Control-Flow Alignments" }, 
	userAccessible = true
	)
public class CheckComplianceBestMatching_Plugin {
	
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Check Temporal Compliance Using Data-Aware Conformance (Match Instances)", requiredParameterLabels = { 0 })
	public Object[] replayLog(UIPluginContext context, PNMatchInstancesRepResult allReplayRes) {
		
		// retrieve connection of the replay result that yields the 
		// log and TransEvClassMapping used for replaying
		PNMatchInstancesRepResultConnection c;
		try {
			c = context.getConnectionManager().getFirstConnection(PNMatchInstancesRepResultConnection.class, context, allReplayRes);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "Could not find connection to log and model of the alignment.");
		}
		
		XLog abstractedLog = c.getObjectWithRole(PNMatchInstancesRepResultConnection.LOG);
		ProvidedObjectID id_abstracedLog = 
				context.getProvidedObjectManager().createProvidedObject(
						XConceptExtension.instance().extractName(abstractedLog), abstractedLog, XLog.class, context);

		GetLogFromBestMatchingAlignments_Plugin getLog = new GetLogFromBestMatchingAlignments_Plugin();
		XLog properLog = getLog.getLogFromAlignment(context, allReplayRes);
		
		ProvidedObjectID id_properLog = 
				context.getProvidedObjectManager().createProvidedObject(
						XConceptExtension.instance().extractName(properLog), properLog, XLog.class, context);

		
		CreateTemporalPattern_Plugin createTemporalPattern = new CreateTemporalPattern_Plugin();
		Object[] tp = createTemporalPattern.createTemporalPattern(context, properLog);
		PetriNetWithData net = (PetriNetWithData)tp[0];
		Marking m_init = (Marking)tp[1];
		Marking m_final = (Marking)tp[2];
		TemporalComplianceRequirement req = (TemporalComplianceRequirement)tp[3];
		
		EnrichLog_Plugin enrichLog = new EnrichLog_Plugin();
		XLog enrichedLog = enrichLog.enrichLog(context, properLog, req);
		
		ProvidedObjectID id_enrichedLog = 
				context.getProvidedObjectManager().createProvidedObject(
						XConceptExtension.instance().extractName(enrichedLog), enrichedLog, XLog.class, context);
		//context.getGlobalContext().getResourceManager().getResourceForInstance(enrichedLog).setFavorite(true);

		
		TemporalAlignment replayer = new TemporalAlignment();
		ResultReplay result = replayer.plugin(context, net, enrichedLog);
		
		return new Object[] { result, enrichedLog };
	}
	
/* --------------------------------------------------------------------------------
 * 
 *    old code for invoking replayer showing the replayer's GUI
 * 
 * --------------------------------------------------------------------------------
 */
//		PNReplayerUI pnReplayerUI = new PNReplayerUI();
//		Object[] resultConfiguration = pnReplayerUI.getConfiguration(context, net, log, mapping);
//		if (resultConfiguration == null) {
//			context.getFutureResult(0).cancel(true);
//			return null;
//		}
//
//		// if all parameters are set, replay log
//		if (resultConfiguration[PNReplayerUI.MAPPING] != null) {
//			context.log("replay is performed. All parameters are set.");
//
//			// This connection MUST exists, as it is constructed by the configuration if necessary
//			context.getConnectionManager().getFirstConnection(EvClassLogPetrinetConnection.class, context, net, log);
//
//			// get all parameters
//			IPNReplayAlgorithm selectedAlg = (IPNReplayAlgorithm) resultConfiguration[PNReplayerUI.ALGORITHM];
//			IPNReplayParameter algParameters = (IPNReplayParameter) resultConfiguration[PNReplayerUI.PARAMETERS];
//
//			// since based on GUI, create connection
//			algParameters.setCreateConn(true);
//			algParameters.setGUIMode(true);
//
//			PNRepResult res = replayLog(context, net, log,
//					(TransEvClassMapping) resultConfiguration[PNReplayerUI.MAPPING], selectedAlg, algParameters);
//
//			context.getFutureResult(0).setLabel(
//					"Compliance diagnostics for " + XConceptExtension.instance().extractName(log) + " on "
//							+ net.getLabel() + " using " + selectedAlg.toString());
//			
//			return res;
//
//		} else {
//			context.log("replay is not performed because not enough parameter is submitted");
//			context.getFutureResult(0).cancel(true);
//			return null;
//		}

	
//	public PNRepResult replayLog(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping,
//			IPNReplayAlgorithm selectedAlg, IPNReplayParameter parameters) {
//		if (selectedAlg.isAllReqSatisfied(context, net, log, mapping, parameters)) {
//			// for each trace, replay according to the algorithm. Only returns two objects
//			PNRepResult replayRes = null;
//
//			if (parameters.isGUIMode()) {
//				long start = System.nanoTime();
//
//				replayRes = selectedAlg.replayLog(context, net, log, mapping, parameters);
//
//				long period = System.nanoTime() - start;
//				NumberFormat nf = NumberFormat.getInstance();
//				nf.setMinimumFractionDigits(2);
//				nf.setMaximumFractionDigits(2);
//
//				context.log("Replay finished in " + nf.format(period / 1000000000) + " seconds");
//			} else {
//				replayRes = selectedAlg.replayLog(context, net, log, mapping, parameters);
//			}
//
//			// add connection
//			if (replayRes != null) {
//				
//				final List<SyncReplayResult> adoptedAlignments = new ArrayList<SyncReplayResult>();
//				// create traces in the aligned log (each trace is one trace class from the replay)
//				for (SyncReplayResult alignment : replayRes) {
//					List<SyncReplayResult> r2 = adoptResult(alignment, mapping);
//					adoptedAlignments.addAll(r2);
//				}
//				PNRepResult adoptedResult = new PNRepResult(adoptedAlignments);
//				
//				if (parameters.isCreatingConn()) {
//					context.addConnection(new PNRepResultAllRequiredParamConnection(
//							"Connection between replay result, " + XConceptExtension.instance().extractName(log)
//									+ ", and " + net.getLabel(), net, log, mapping, selectedAlg, parameters, adoptedResult));
//				}
//				return adoptedResult;
//			} else {
//				return null;
//			}
//
//		} else {
//			if (context != null) {
//				context.log("The provided parameters is not valid for the selected algorithm.");
//				context.getFutureResult(0).cancel(true);
//			}
//			return null;
//		}
//	}
//	
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

	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[Control-Flow Compliance]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
	
}
