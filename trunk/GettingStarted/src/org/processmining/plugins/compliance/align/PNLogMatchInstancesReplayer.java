/**
 * 
 */
package org.processmining.plugins.compliance.align;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.tue.astar.AStarException;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.connections.petrinets.EvClassLogPetrinetConnection;
import org.processmining.models.connections.petrinets.PNMatchInstancesRepResultConnection;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.InhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetGraph;
import org.processmining.models.graphbased.directed.petrinet.ResetInhibitorNet;
import org.processmining.models.graphbased.directed.petrinet.ResetNet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.connectionfactories.logpetrinet.TransEvClassMapping;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.algorithms.IPNReplayParameter;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.IPNMatchInstancesLogReplayAlgorithm;
import org.processmining.plugins.petrinet.replayer.matchinstances.algorithms.express.AllOptAlignmentsTreeAlg;
import org.processmining.plugins.petrinet.replayresult.PNMatchInstancesRepResult;

/**
 * @author aadrians
 * 
 */
public class PNLogMatchInstancesReplayer extends org.processmining.plugins.petrinet.replayer.matchinstances.PNLogMatchInstancesReplayer {
	
	private IPNReplayAlgorithm	 usedAlgorithm;
	private IPNReplayParameter   usedAlgParameters;
	private TransEvClassMapping  usedMapping;
	

	public IPNReplayAlgorithm getUsedAlgorithm() {
		return usedAlgorithm;
	}

	public IPNReplayParameter getUsedAlgParameters() {
		return usedAlgParameters;
	}
	
	public TransEvClassMapping getUsedMapping() {
		return usedMapping;
	}

	// dummy event class (for unmapped transitions)
	public final static XEventClass DUMMY = new XEventClass("DUMMY", -1);
	
	public static TransEvClassMapping getEventClassMapping(PluginContext context, PetrinetGraph net, XLog log, XEventClassifier classifier) {
		
		TransEvClassMapping map = new TransEvClassMapping(classifier, DUMMY);
		
		List<XEventClass> eventClasses = getEventClasses(log, classifier);
		for (Transition t : net.getTransitions()) {
			if (t.isInvisible()) {
				map.put(t, DUMMY);
			} else {
				XEventClass match = getEventClassMapping_preSelectOption(t.getLabel(), eventClasses);
				if (match != null) {
					map.put(t, match);
				} else {
					map.put(t, DUMMY);
				}
			}
		}
		
		// mapping is finished, create connection
		EvClassLogPetrinetConnection con = new EvClassLogPetrinetConnection("Connection between " + net.getLabel() + " and "
				+ XConceptExtension.instance().extractName(log), net, log, XLogInfoImpl.NAME_CLASSIFIER, map);
		context.addConnection(con);
		
		return map;
	}
	
	/**
	 * Adopt an existing transition to event class mapping between an old net
	 * and the given log, to a new mapping between the given net and log. Reuses
	 * the original event classifier and the original dummy event class.
	 * 
	 * @param context
	 * @param net
	 * @param log
	 * @param old_map
	 * @param createConnection
	 * @return mapping between net and log
	 */
	public static TransEvClassMapping adoptEventClassMapping(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping old_map, boolean createConnection) {
		XEventClassifier classifier = old_map.getEventClassifier();
		XEventClass dummy = old_map.getDummyEventClass();
		
		List<XEventClass> eventClasses = new ArrayList<XEventClass>();
		for (XEventClass e : old_map.values()) eventClasses.add(e);
		
		TransEvClassMapping map = new TransEvClassMapping(classifier, dummy);
		for (Transition t : net.getTransitions()) {
			if (t.isInvisible()) {
				map.put(t, dummy);
			} else {
				String name = t.getLabel();
				//List<Transition> t_olds = new ArrayList<Transition>();
				XEventClass e_old = null;
				for (Transition t_old : old_map.keySet()) {
					if (t_old.getLabel().equals(name)) {
						//t_olds.add(t_old);
						if (e_old != null && e_old != old_map.get(t_old)) {
							System.out.println("Warning: "+name+" can be mapped to different event classes "+e_old+" and "+old_map.get(t_old));
						}
						e_old = old_map.get(t_old);
					}
				}
				if (e_old != null) {
					map.put(t, e_old);
				} else {
					XEventClass preSelect =  getEventClassMapping_preSelectOption(name, eventClasses);
					if (preSelect != null) map.put(t, preSelect);
					else map.put(t, dummy);
				}
			}
		}
		
		
		// mapping is finished, create connection
		EvClassLogPetrinetConnection con = new EvClassLogPetrinetConnection("Connection between " + net.getLabel() + " and "
				+ XConceptExtension.instance().extractName(log), net, log, XLogInfoImpl.NAME_CLASSIFIER, map);
		context.addConnection(con);
		
		return map;
	}
	
	/**
	 * Returns the Event Option Box index of the most similar event for the
	 * transition.
	 * 
	 * @param transition
	 *            Name of the transitions
	 * @param events
	 *            Array with the options for this transition
	 * @return Index of option more similar to the transition
	 */
	public static XEventClass getEventClassMapping_preSelectOption(String transition, Collection<XEventClass> eventClasses) {
		for (XEventClass cl : eventClasses) {
			if (cl == DUMMY) continue;
			if (transition.startsWith(cl.toString())) return cl;		
		}
		return null;
	}
	
	public static List<XEventClass> getEventClasses(XLog log, XEventClassifier classifier) {
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		XEventClasses eventClasses = summary.getEventClasses();
		
		List<XEventClass> classes = new ArrayList<XEventClass>(eventClasses.getClasses());
		classes.add(0, DUMMY);
		
		return classes;
	}
	
	public static class ReplayParams {
		public IPNMatchInstancesLogReplayAlgorithm selectedAlg;
		public Map<XEventClass, Integer> mapEvClass2Cost;
		public Map<Transition, Integer> mapTrans2Cost;
		public Map<Transition, Integer> mapSyncCost;
		
		public Object[] parameters;

		public Marking m_initial;
		public Marking m_final;
	}
	
	// create map trans to cost
//	res[ExpressILPAlg.MAPTRANSTOCOST] = getTransitionWeight();
//	res[ExpressILPAlg.MAXEXPLOREDINSTANCES] = limExpInstances.getValue() == MAXLIMMAXNUMINSTANCES ? Integer.MAX_VALUE
//			: limExpInstances.getValue() * 100;
//	res[ExpressILPAlg.MAPXEVENTCLASSTOCOST] = getMapEvClassToCost();

	
	public static ReplayParams getReplayerParameters(PluginContext context, PetrinetGraph net, XLog log, XEventClassifier classifier, Marking m_initial, Marking m_final) {
		
		ReplayParams rParams = new ReplayParams();
		
		rParams.selectedAlg = new AllOptAlignmentsTreeAlg();
		List<XEventClass> eventClasses = getEventClasses(log, classifier);
		rParams.mapEvClass2Cost = new HashMap<XEventClass, Integer>();
		for (XEventClass cl : eventClasses) {
			rParams.mapEvClass2Cost.put(cl, 1);
		}

		rParams.mapTrans2Cost = new HashMap<Transition, Integer>();
		for (Transition t : net.getTransitions()) {
			if (t.isInvisible()) rParams.mapTrans2Cost.put(t, 0);
			else rParams.mapTrans2Cost.put(t, 1);
		}
		
		rParams.m_initial = m_initial;
		rParams.m_final = m_final;
		
		rParams.parameters = new Object[3];
		rParams.parameters[AllOptAlignmentsTreeAlg.MAPTRANSTOCOST] = rParams.mapTrans2Cost;
		rParams.parameters[AllOptAlignmentsTreeAlg.MAPXEVENTCLASSTOCOST] = rParams.mapEvClass2Cost;
		rParams.parameters[AllOptAlignmentsTreeAlg.MAXEXPLOREDINSTANCES] = 200000;
		
		return rParams;
	}
	
	public static PNMatchInstancesRepResult callReplayer(PluginContext context, PetrinetGraph net, XLog log, TransEvClassMapping mapping, ReplayParams par, boolean createConnection) {
		PNLogMatchInstancesReplayer replayer = new PNLogMatchInstancesReplayer();
		System.out.println("replaying");
		PNMatchInstancesRepResult res = null;
		
		if (net instanceof ResetInhibitorNet)
			try {
				res = replayer.replayLog(context, (ResetInhibitorNet)net, log, mapping, par.m_initial, par.m_final, par.selectedAlg, par.parameters);
			} catch (AStarException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		else if (net instanceof ResetNet)
			try {
				res = replayer.replayLog(context, (ResetNet)net, log, mapping, par.m_initial, par.m_final, par.selectedAlg, par.parameters);
			} catch (AStarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else if (net instanceof InhibitorNet)
			try {
				res = replayer.replayLog(context, (InhibitorNet)net, log, mapping, par.m_initial, par.m_final, par.selectedAlg, par.parameters);
			} catch (AStarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else if (net instanceof Petrinet)
			try {
				res = replayer.replayLog(context, (Petrinet)net, log, mapping, par.m_initial, par.m_final, par.selectedAlg, par.parameters);
			} catch (AStarException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		else
			return null;
		
		if (createConnection) {
			// add connection
			PNMatchInstancesRepResultConnection con = context.addConnection(new PNMatchInstancesRepResultConnection(
					"All results of replaying " + XConceptExtension.instance().extractName(log) + " on "
							+ net.getLabel(), net, par.m_initial, log, res));
			con.setLabel("Connection between " + net.getLabel() + ", " + XConceptExtension.instance().extractName(log)
					+ ", and matching instances");
		}
		return res;
	}
	
	public static Object[] constructFinalMarking(PluginContext context, PetrinetGraph net) {
		
		Marking finalMarking = new Marking();
		for (Place p : net.getPlaces()) {
			if (net.getOutEdges(p).size() == 0) finalMarking.add(p);
		}
		
		Collection<Place> colPlaces = net.getPlaces();
		for (Place p : finalMarking) {
			if (!colPlaces.contains(p)) {
				throw new IllegalArgumentException("Final marking contains places outside of the net");
			}
		}
		FinalMarkingConnection conn = new FinalMarkingConnection(net, finalMarking);
		context.addConnection(conn);
		return new Object[] { conn , finalMarking };
	}

	/**
	 * Create a final marking for the given net based on a final marking of another net.
	 * @param context
	 * @param net
	 * @param old_m_final
	 * @param createConnection	whether to create a {@link FinalMarkingConnection} between {@code net} and new final marking
	 * @return new final making 
	 */
	public static Marking adoptFinalMarking(PluginContext context, PetrinetGraph net, Marking old_m_final, boolean createConnection) {
		
		Marking finalMarking = new Marking();
		for (Place p : net.getPlaces()) {
			// a place of the given net gets marked if there is a place with the same name in the old marking
			for (Place p_old : old_m_final.baseSet()) {
				if (p_old.getLabel().equals(p.getLabel())) finalMarking.add(p);
			}
		}
		
		// sanity check for correctness of final marking
		Collection<Place> colPlaces = net.getPlaces();
		for (Place p : finalMarking) {
			if (!colPlaces.contains(p)) {
				throw new IllegalArgumentException("Final marking contains places outside of the net");
			}
		}
		
		// create connection if desired
		if (createConnection) {
			FinalMarkingConnection conn = new FinalMarkingConnection(net, finalMarking);
			context.addConnection(conn);
		}
		return finalMarking;
	}
	
}
