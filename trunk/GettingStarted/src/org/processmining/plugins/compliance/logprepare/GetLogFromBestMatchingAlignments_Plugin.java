package org.processmining.plugins.compliance.logprepare;

import java.util.ArrayList;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.PNMatchInstancesRepResultConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.compliance.AbstractLogForCompliance_Plugin;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.AllSyncReplayResult;

@Plugin(name = "Get Log From Best-Matching Alignments",
	returnLabels = { "Best-Matching Alignments" },
	returnTypes = { XLog.class },
	parameterLabels = {"Alignment"}, 
	help = "Translates the control-flow alignment into a log.", userAccessible = true
	)
public class GetLogFromBestMatchingAlignments_Plugin {
	

	
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Get Log From Best-Matching Alignments", requiredParameterLabels = { 0 })
	public XLog getLogFromAlignment(PluginContext context, PNMatchInstancesRepResult result) {
		
		// retrieve connection of the replay result that yields the 
		// log and TransEvClassMapping used for replaying
		PNMatchInstancesRepResultConnection c;
		try {
			c = context.getConnectionManager().getFirstConnection(PNMatchInstancesRepResultConnection.class, context, result);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "Could not find connection to log and model of the alignment.");
		}

		XLog log = (XLog)c.getObjectWithRole(PNMatchInstancesRepResultConnection.LOG);
		PetrinetGraph net = (PetrinetGraph)c.getObjectWithRole(PNMatchInstancesRepResultConnection.PN);
		TransEvClassMapping map;
		try {
			map = context.tryToFindOrConstructFirstObject(TransEvClassMapping.class, EvClassLogPetrinetConnection.class, EvClassLogPetrinetConnection.TRANS2EVCLASSMAPPING, net, log);
		} catch (ConnectionCannotBeObtained e) {
			return cancel(context, "Could not find mapping between log and model.");
		}

		// create aligned log
		XFactory f = XFactoryRegistry.instance().currentDefault();
		
		XLog alignedLog = f.createLog();
		
		// log needs a name
		String alignedLogName = log.getAttributes().get("concept:name").toString()+" (control-flow aligned)";
		XAttributeMap logAttr = f.createAttributeMap();
		logAttr.put("concept:name",
					   f.createAttributeLiteral("concept:name", alignedLogName, XConceptExtension.instance()));
		alignedLog.setAttributes(logAttr);
		
		// create traces in the aligned log (each trace is one trace class from the replay)
		for (AllSyncReplayResult res : result) {
			
			for (int resNum=0; resNum < res.getNodeInstanceLst().size(); resNum++) {
		
				// collect event order as determined by replayer
				ArrayList<Object> alignedEvents = new ArrayList<Object>(res.getNodeInstanceLst().get(resNum));
				ArrayList<StepTypes> stepTypes = new ArrayList<StepTypes>(res.getStepTypesLst().get(resNum));
				
				// to preserve frequencies of the original log, create a separate copy
				// for each trace in the trace class: collect all caseIDs in the class
				for (int index : res.getTraceIndex()) {
					
					XTrace ot = log.get(index);
				
					// copy trace attributes: we are going to modify them
					XAttributeMap t_attributes = f.createAttributeMap();
					t_attributes.putAll(ot.getAttributes());
					
					// create trace
					XTrace t = f.createTrace(t_attributes);
					
					int ot_eventNum = 0;
					
					// add events to trace
					for (int i=0; i<alignedEvents.size(); i++) {
						// skip log events that cannot be replayed on the model
						//if (res.getStepTypes().get(i) == org.processmining.plugins.replayer.util.StepTypes.L) continue;
						
						XEvent e = null;
						
						Object aligned = alignedEvents.get(i);
						StepTypes stepType = stepTypes.get(i);
						
						if (   stepType == org.processmining.plugins.petrinet.replayresult.StepTypes.L
							|| stepType == org.processmining.plugins.petrinet.replayresult.StepTypes.LMGOOD
							|| stepType == org.processmining.plugins.petrinet.replayresult.StepTypes.LMNOGOOD)
						{
							// log moves: object is an XEvent
							XEvent oe = ot.get(ot_eventNum++);
							e = f.createEvent(oe.getAttributes());
						} else {
							// log moves: object is a Transition
							
							// skip invisible transitions from the event log
							if (aligned instanceof Transition && isInvisibleTransition((Transition)aligned))
								continue;
							
							e = f.createEvent(f.createAttributeMap());
							setEventNameAndLifeCycle(e, aligned, stepType, map, f);
						}
	
						setMoveTypeAttribute(e, stepType, f);
						setTimeStamp(e, ot, ot_eventNum, stepType, f);
						
						// add event to trace
						t.add(e);
					}
					
					String traceName = XConceptExtension.instance().extractName(ot);
					XConceptExtension.instance().assignName(t, traceVariant_getCombinedName(traceName, resNum));
				
					// add trace to log
					alignedLog.add(t);
				}
			}
		}
		
		context.addConnection(new GetLogFromBestMatchingAlignments_Connection("Connection from "+alignedLogName+" to its originative alignment", result, alignedLog));
		
  		context.getFutureResult(0).setLabel(alignedLogName);
		return alignedLog;
	}

	/**
	 * @param traceName
	 * @param variant
	 * @return trace name that distinguishes original trace name and the trace variant coming from the given alignment
	 */
	public static String traceVariant_getCombinedName(String traceName, int variant) {
		return traceName+"_var"+variant;
	}
	
	/**
	 * @param combinedName
	 *            produced by {@link #traceVariant_getCombinedName(String, int)}
	 * @return original trace name contained in the combinedName, or
	 *         combinedName if combinedName was not produced by
	 *         {@link #traceVariant_getCombinedName(String, int)}
	 */
	public static String traceVariant_getOriginalName(String combinedName) {
		if (combinedName.lastIndexOf("_var") > 0)
			return combinedName.substring(0, combinedName.lastIndexOf("_var"));
		else
			return combinedName;
	}
	
	/**
	 * Set time stamp for event e, in case e has no timestamp yet. The timestamp
	 * is derived from the timestamp of the preceding or the succeeding event in
	 * the given 'trace', where the current position in the trace is given by
	 * the 'index'
	 * 
	 * @param e
	 * @param trace
	 * @param index
	 * @param f
	 */
	private static void setTimeStamp(XEvent e, XTrace trace, int index, StepTypes type, XFactory f) {
		// check if timestamp is already set, if yes, keep it
		if (XTimeExtension.instance().extractTimestamp(e) != null) return;
		
		java.util.Date timeStamp = null;
		String eventName = XConceptExtension.instance().extractName(e);
		if (eventName.equals(AbstractLogForCompliance_Plugin.INSTANCE_START_TRANSITION)) {
			if (index < trace.size()) {
				XEvent nextEvent = trace.get(index);
				if (nextEvent != null) {
					timeStamp = XTimeExtension.instance().extractTimestamp(nextEvent);
				}
			}
		} else if (eventName.equals(AbstractLogForCompliance_Plugin.INSTANCE_COMPLETE_TRANSITION)) {
			if (index > 0) {
				XEvent previousEvent = trace.get(index-1);
				if (previousEvent != null) {
					timeStamp = XTimeExtension.instance().extractTimestamp(previousEvent);
				}
			}
		} else if (type == StepTypes.MREAL) {
			// for visible model moves, search for the preceding event with a timestamp and
			// use this timestamp for the model move 
			for (int otherIndex = index-1; otherIndex >= 0; otherIndex--) {
				XEvent previousEvent = trace.get(otherIndex);
				if (previousEvent != null) {
					timeStamp = XTimeExtension.instance().extractTimestamp(previousEvent);
					if (timeStamp != null) break;
				}
			}
		}
		
		if (timeStamp != null) {
			XTimeExtension.instance().assignTimestamp(e, timeStamp);
		}
	}
	
	/**
	 * @param t
	 * @return {@code true} iff the transition is an invisible transition
	 */
	public static boolean isInvisibleTransition(Transition t) {
		if (t.getLabel().equalsIgnoreCase("tau") || t.getLabel().equals("")) return true;
		return false;
	}
	
	private static void setMoveTypeAttribute(XEvent e, StepTypes type, XFactory f) {
		String type_string = "undefined";
		switch (type) {
			case L : type_string = "L"; break;
			case LMGOOD : type_string = "LMGOOD"; break;
			case LMNOGOOD : type_string = "LMNOGOOD"; break;
			case LMREPLACED : type_string = "LMREPLACED"; break;
			case LMSWAPPED : type_string = "LMSWAPPED"; break;
			case MINVI : type_string = "MINVI"; break;
			case MREAL : type_string = "MREAL"; break;
		}
		
		XAttributeLiteral lit = f.createAttributeLiteral(GetLogFromControlFlowAlignment_Plugin.KEY_COMPLIANCE_MOVETYPE, type_string, null);
		e.getAttributes().put(GetLogFromControlFlowAlignment_Plugin.KEY_COMPLIANCE_MOVETYPE, lit);
	}
	
	private static void setEventNameAndLifeCycle(XEvent e, Object aligned, StepTypes type, TransEvClassMapping map, XFactory f) {
		// split name into event name and life-cycle transition
		String qualified_eventName;
		
		if (type == StepTypes.MREAL && map.containsKey(aligned) && map.get(aligned) != map.getDummyEventClass()) {
			// if the step was a model move, retrieve the real event that was mapped to it
			// and name the "model move" event after this event, i.e., repair the log for
			// missing events
			qualified_eventName = map.get(aligned).getId();
		} else {
			qualified_eventName = aligned.toString();
		}
		
		String name;
		String life_cycle;
		int plus_pos = qualified_eventName.indexOf('+');
		if (plus_pos >= 0) {
			name = qualified_eventName.substring(0, plus_pos);
			life_cycle = qualified_eventName.substring(plus_pos+1);
		} else {
			name = qualified_eventName;
			life_cycle = "complete";
		}
		
		XConceptExtension.instance().assignName(e, name);
		XLifecycleExtension.instance().assignTransition(e, life_cycle);
	}
	
	protected static XLog cancel(PluginContext context, String message) {
		System.out.println("[get log from alignment]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
}
