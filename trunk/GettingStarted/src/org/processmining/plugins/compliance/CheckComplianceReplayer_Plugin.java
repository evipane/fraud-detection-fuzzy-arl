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
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.fraud.model.InsertFraudData;
import org.processmining.fraud.model.fraud;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.compliance.align.PNLogReplayer;
import org.processmining.plugins.compliance.align.PNLogReplayer.ReplayParams;
import org.processmining.plugins.compliance.temporal.CreateTemporalPattern_Plugin;
import org.processmining.plugins.compliance.ui.CheckConformanceValue;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.petrinet.replayresult.visualization.PNLogReplayResultVisPanel;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

/**
 * @author aadrians Feb 13, 2012
 * 
 */
@Plugin(name = "Check Compliance Using Conformance Checking",
	returnLabels = { "Compliance Diagnostics", "Abstracted Log","Fraud Data" },
	returnTypes = { PNRepResult.class, XLog.class, InsertFraudData.class},
	parameterLabels = {"Petri net", "Event Log" }, 
	help = "Replay an event log on Petri net to get all manifest of patterns.", userAccessible = true
	)
public class CheckComplianceReplayer_Plugin extends PNLogReplayResultVisPanel{
	/**
	 * 
	 */
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Check Compliance Using Conformance Checking", requiredParameterLabels = { 0, 1 })
	public Object[] replayLog(UIPluginContext context, PetrinetGraph net,  XLog originalLog) {
		
		for (Transition t : net.getTransitions()) {
			if (t.getLabel().toLowerCase().equalsIgnoreCase("tau") || t.getLabel().toLowerCase().startsWith("final")
				|| t.getLabel().toLowerCase().equals(CreateTemporalPattern_Plugin.T_INSTANCE_START.toLowerCase())
				|| t.getLabel().toLowerCase().equals(CreateTemporalPattern_Plugin.T_INSTANCE_COMPLETE.toLowerCase())
				|| t.getLabel().toLowerCase().equals(CreateTemporalPattern_Plugin.T_START.toLowerCase())
				|| t.getLabel().toLowerCase().equals(CreateTemporalPattern_Plugin.T_END.toLowerCase()))
			{
				t.setInvisible(true);
			}
		}
		
		Object[] abstraction = AbstractLogForCompliance_Plugin.connectLogToPetriNet(context, originalLog, net);
		XLog log = (XLog)abstraction[0];
		TransEvClassMapping mapping = (TransEvClassMapping)abstraction[1];
		CheckConformanceValue check = new CheckConformanceValue();
		List<fraud>Fraud = new ArrayList<fraud>();
		InsertFraudData ifd = new InsertFraudData();
		Marking m_initial;
		try {
			m_initial = context.tryToFindOrConstructFirstObject(Marking.class, InitialMarkingConnection.class, InitialMarkingConnection.MARKING, net);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "No initial marking found.");
		}
		
		Marking m_final = (Marking)PNLogReplayer.constructFinalMarking(context, net)[1];
		
		ReplayParams par = PNLogReplayer.getReplayerParameters(context, net, log, mapping.getEventClassifier(), m_initial, m_final);
		for (XEventClass e : par.mapEvClass2Cost.keySet()) {
			par.mapEvClass2Cost.put(e, 100);
		}
		par.parameters.setCreateConn(true);
		PNRepResult res = PNLogReplayer.callReplayer(context, net, log, mapping, par);
		
		context.getFutureResult(0).setLabel(
			"Control-flow compliance alignment for " + XConceptExtension.instance().extractName(log) + " on "
					+ net.getLabel() + " using " + par.selectedAlg.toString());
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String timeString = dateFormat.format(date);
		
		String logName = log.getAttributes().get("concept:name").toString()+" (abstracted @ "+timeString+")";
		context.getFutureResult(1).setLabel(logName);
		
		ifd.insert(check.visualization(context, res));
		System.out.println("Sizes: "+ifd.frauds.size());
		//System.out.println("Tabel Fraud: "+Fraud.get(0).getCase()+" -- "+Fraud.get(0).getSkipSeq());
		//System.out.println("Jumlah SKip: "+skipped);
		return new Object[] { res, log,ifd };
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
