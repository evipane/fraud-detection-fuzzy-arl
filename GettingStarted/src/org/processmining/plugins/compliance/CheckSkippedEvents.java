package org.processmining.plugins.compliance;
/**
 * 
 */
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JPanel;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
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
 * @author Farid Feb 9, 2014
 * 
 */
@Plugin(name = "Check Skipped Events Using Conformance Checking",
		returnLabels ={"Skipped Events"},
		returnTypes = {JPanel.class},
		parameterLabels = {"Petri net", "Event Log" }, 
		userAccessible = true
	)
	
public class CheckSkippedEvents extends PNLogReplayResultVisPanel{


	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Farid Naufal",
			email = "naufalfarid99@gmail.com",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Check Skipped Events Using Conformance Checking", requiredParameterLabels = { 0, 1 })
	public Object[] replayLog(UIPluginContext context, PetrinetGraph net,  XLog originalLog) {
		
		JPanel panel = new JPanel();
		
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
		Map<String, Integer> skipped = new HashMap<String, Integer>();
		List<fraud>Fraud = new ArrayList<fraud>();
		fraud frauds = null;
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
		
		//skipped = check.visualization(context, res);
		Set<String> mapinfo = skipped.keySet();
		
		for(String property : mapinfo)
		{
			//frauds = new fraud(1, skipped.get(property), 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
			Fraud.add(frauds);
		}
		//Fraud.add(frauds);
		System.out.println("Tabel Fraud: "+Fraud.get(0).getCase()+" -- "+Fraud.get(0).getSkipSeq());
		System.out.println("Jumlah SKip: "+skipped);
		return new Object[] { res, log };
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

	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[Control-Flow Compliance]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
	
}
