/**
 * 
 */
package org.processmining.plugins.compliance;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.manifestreplayer.TransClass2PatternMap;
import org.processmining.plugins.petrinet.manifestreplayer.transclassifier.TransClass;


/**
 * @author aadrians
 *
 */
@Plugin(name = "Abstract Log for Compliance Checking", 
	parameterLabels = { "Log", "Compliance Pattern" },
	returnLabels = {"abstracted log" , "mapping"},
	returnTypes = { XLog.class, TransEvClassMapping.class },
	mostSignificantResult = 1,
	userAccessible = true)
public class AbstractLogForCompliance_Plugin {
	
	@UITopiaVariant(
			affiliation="TU/e",
			author="D. Fahland",
			email="d.fahland@tue.nl",
			website = "http://www.processmining.org/",
			pack="Compliance")
	@PluginVariant(variantLabel = "Abstract Log for Compliance Checking", requiredParameterLabels = { 0, 1 })
	public Object[] connect(UIPluginContext context, XLog log, PetrinetGraph net) {

		Object[] result = connectLogToPetriNet(context, log, net);
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String timeString = dateFormat.format(date);
		
		String logName = log.getAttributes().get("concept:name").toString()+" (abstracted @ "+timeString+")";
		context.getFutureResult(0).setLabel(logName);
		context.getFutureResult(1).setLabel("Mapping from " + net.getLabel() + " to " + XConceptExtension.instance().extractName((XLog)result[0]));

		return result;
	}
	
	public static Object[] connectLogToPetriNet(UIPluginContext context, XLog log, PetrinetGraph net) {
		// list possible classifiers
		XEventClassifier[] availableEventClass = new XEventClassifier[4];
		availableEventClass[0] = XLogInfoImpl.STANDARD_CLASSIFIER; 
		availableEventClass[1] = XLogInfoImpl.NAME_CLASSIFIER; 
		availableEventClass[2] = XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER; 
		availableEventClass[3] = XLogInfoImpl.RESOURCE_CLASSIFIER; 
		
		// build and show the UI to make the mapping
		MapEvPattern2Trans_Smart_UI ui = new MapEvPattern2Trans_Smart_UI(log, net, availableEventClass);
		InteractionResult result = context.showWizard("Mapping Petrinet - Event Class of Log", true, true, ui);

		// create the connection or not according to the button pressed in the UI
		if (result == InteractionResult.FINISHED) {
			HashMap<XEvent, XEvent> abstractToOriginal = new HashMap<XEvent, XEvent>();
			XLog abstractedLog = abstractLog(log, net, ui.getPatternMap(), ui.getEventClasses(), abstractToOriginal);
			TransEvClassMapping map = adoptMap(ui.getEventMap(), ui.getSelectedClassifier(), abstractedLog);
			
			EvClassLogPetrinetConnection con = new EvClassLogPetrinetConnection(
					"Connection between " + net.getLabel() + " and " + XConceptExtension.instance().extractName(log), net, abstractedLog,
					ui.getSelectedClassifier(), map);
			context.getConnectionManager().addConnection(con);
			
			AbstractLogForCompliance_Connection con2 = new AbstractLogForCompliance_Connection(
					"Connection between "+XConceptExtension.instance().extractName(log)+" and abstracted log (by "+net.getLabel()+")", 
					log, net, abstractedLog);
			context.getConnectionManager().addConnection(con2);

			return new Object[] { abstractedLog, map, abstractToOriginal };
		} else {
			return null;
		}
	}
	
	public static TransEvClassMapping adoptMap(TransEvClassMapping _map, XEventClassifier chosenClassifer, XLog abstractedLog) {
		
		TransEvClassMapping map = new TransEvClassMapping(chosenClassifer, AbstractLogForCompliance_Plugin.DUMMY);
		XLogInfo summary = XLogInfoFactory.createLogInfo(abstractedLog, chosenClassifer);
		XEventClasses eventClasses = summary.getEventClasses();
		
		for (Map.Entry<Transition, XEventClass> m : _map.entrySet()) {
			if (m.getValue() == OMEGA_CLASS) {
				map.put(m.getKey(), OMEGA_CLASS);	
			} else {
				XEventClass newClass = eventClasses.getByIdentity(m.getValue().getId());
				if (newClass != null)
					map.put(m.getKey(), newClass);
				else
					map.put(m.getKey(), AbstractLogForCompliance_Plugin.DUMMY);
			}
		}
		return map;
		
	}
	
	/**
	 * Resolve the given transition class to event pattern mapping to a mapping
	 * that returns for each event class the set of events that this transition
	 * class is mapped to in standard Java Colletions.
	 * 
	 * @param net
	 * @param map
	 * @return
	 */
	private static Map<TransClass, Set<XEventClass>> getTransClass2EventMapping(PetrinetGraph net, TransClass2PatternMap map) {
		
		Map<TransClass, Set<XEventClass>> t2x = new HashMap<TransClass, Set<XEventClass>>();
		
		
		//TransClass[] tClasses = map.getTransClassEnc();
		XEventClass[] eClasses = map.getEvClassEnc();
		for (XEventClass eClass : eClasses) {
			short enc = map.getEvClassEncFor(eClass);
			//System.out.println(eClass+" -> "+enc);
		}
		
		for (Transition t : net.getTransitions()) {
			short[] patterns = map.getPatternsOf(t);
			HashSet<XEventClass> classes = new HashSet<XEventClass>();
			if (patterns != null) {
				int j=0;
				while (j<patterns.length) {
					for (int i=0; i<patterns[j+1]; i++) {
						XEventClass eClass = eClasses[patterns[j+2+i]];
						if (eClass != DUMMY) classes.add(eClass);
					}
					j += patterns[j+1]+2;
				}
			}
			TransClass tc = map.getTransClassOf(t);
			if (!t2x.containsKey(tc)) t2x.put(tc, new HashSet<XEventClass>());
			t2x.get(tc).addAll(classes);
		}
		
		return t2x;
	}
	
	public static final String OMEGA_TRANSITION = "omega";
	
	public static final String INSTANCE_START_TRANSITION = "I_st";
	public static final String INSTANCE_COMPLETE_TRANSITION = "I_cmp";
	
	public static final String OMEGA_CLASS_NAME = "other";
	
	/**
	 * Dummy event class to represent all events not mapped to a visible transition
	 * in a compliance pattern.
	 */
	public final static XEventClass OMEGA_CLASS = new XEventClass(OMEGA_CLASS_NAME+"+", -17);
	public final static XEventClass OMEGA_CLASS_NO_LIFECYCLE = new XEventClass(OMEGA_CLASS_NAME, -18);
	
	public static XEventClass getOmegaClass(XEventClassifier classifier) {
		if (classifier == XLogInfoImpl.NAME_CLASSIFIER || 
			classifier == XLogInfoImpl.RESOURCE_CLASSIFIER)
		{
			return OMEGA_CLASS_NO_LIFECYCLE;
		}
		else
		{
			return OMEGA_CLASS;
		}
	}
	
	/**
	 * Dummy event class for invisible transition
	 */
	public final static XEventClass DUMMY = new XEventClass("DUMMY", -1);
	
	/**
	 * @param t2x
	 * @return a map the map an event class to itself if it is the image of a
	 *         visible transition in the pattern, and that maps an event class
	 *         to {@link #OMEGA_CLASS} if it is the image of the
	 *         {@value #OMEGA_CLASS}-labeled transition in the pattern.
	 */
	private static Map<XEventClass, XEventClass> getAbstractionMapping(Map<TransClass, Set<XEventClass>> t2x) {
		
		Map<XEventClass, XEventClass> abstraction = new HashMap<XEventClass, XEventClass>();
		for (TransClass tc : t2x.keySet()) {
			if (tc.getId().toLowerCase().equals(OMEGA_TRANSITION)) {
				for (XEventClass ec : t2x.get(tc)) {
					abstraction.put(ec, OMEGA_CLASS);
				}
			} else {
				for (XEventClass ec : t2x.get(tc)) {
					abstraction.put(ec, ec);
				}
			}
		}
		
		return abstraction;
	}
	
	public static XLog abstractLog(XLog log, PetrinetGraph net, TransClass2PatternMap map, XEventClasses eventClasses, Map<XEvent, XEvent> abstractToOriginal) {
		/*
		// list possible classifiers
		XEventClassifier[] availableEventClass = new XEventClassifier[4];
		availableEventClass[0] = XLogInfoImpl.STANDARD_CLASSIFIER; 
		availableEventClass[1] = XLogInfoImpl.NAME_CLASSIFIER; 
		availableEventClass[2] = XLogInfoImpl.LIFECYCLE_TRANSITION_CLASSIFIER; 
		availableEventClass[3] = XLogInfoImpl.RESOURCE_CLASSIFIER; 
		
		// build and show the UI to make the mapping
		MapEvPattern2Trans_Smart_UI ui = new MapEvPattern2Trans_Smart_UI(log, net, availableEventClass);
		InteractionResult result = context.showWizard("Mapping Petrinet - Event Class of Log", true, true, ui);

		// create the connection or not according to the button pressed in the UI
		EvClassLogPetrinetConnection con = null;
		if (result == InteractionResult.FINISHED) {
		*/
			//TransClass2PatternMap map = ui.getMap();
			
			XFactory f = XFactoryRegistry.instance().currentDefault();
			XLog abstractedLog = f.createLog();
			abstractedLog.setAttributes(log.getAttributes());
			
			Map<TransClass, Set<XEventClass>> t2x = getTransClass2EventMapping(net, map);
			Map<XEventClass, XEventClass> abstraction = getAbstractionMapping(t2x);
			
			
			for (XTrace trace : log) {
				XTrace tNew = f.createTrace();
				tNew.setAttributes(trace.getAttributes());
				
				int omega_sequence_count = 0;
				
				for (XEvent event : trace) {
					XEvent eNew = f.createEvent();
					eNew.setAttributes(event.getAttributes());
					
					XEventClass abstractClass = abstraction.get(eventClasses.getClassOf(event));
					if (abstractClass == OMEGA_CLASS) {
						omega_sequence_count++;
						if (omega_sequence_count > 3) { // skip if there are more than 3 succeesive OMEGA_CLASS events
							continue;
						}
					} else {
						omega_sequence_count = 0;
					}
					
					String eventName = abstractClass.getId();
					if (eventName.indexOf("+") >= 0) {
						eventName = eventName.substring(0, eventName.indexOf("+"));
					}
					
					// abstract the name of the event
					eNew.getAttributes().put("concept:name",
								   f.createAttributeLiteral("concept:name", eventName,
								   XConceptExtension.instance()));
					if (abstractClass == OMEGA_CLASS) {
						eNew.getAttributes().remove("lifecycle:transition");
					}
					
					tNew.add(eNew);
					abstractToOriginal.put(eNew, event);
				}
					
				abstractedLog.add(tNew);
			}
			
			return abstractedLog;
		/*
		} else {
			return null;
		}*/
	}
	
}
