package org.processmining.plugins.compliance.temporal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.connections.petrinets.behavioral.FinalMarkingConnection;
import org.processmining.models.connections.petrinets.behavioral.InitialMarkingConnection;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.graphbased.directed.petrinet.impl.PetrinetFactory;
import org.processmining.models.graphbased.directed.petrinetwithdata.newImpl.DataElement;
import org.processmining.models.graphbased.directed.petrinetwithdata.newImpl.PetriNetWithData;
import org.processmining.models.graphbased.directed.petrinetwithdata.newImpl.PetriNetWithDataFactory;
import org.processmining.models.guards.Expression;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.compliance.AbstractLogForCompliance_Plugin;

@Plugin(name = "Create Temporal Pattern for Compliance Checking",
    parameterLabels = { "log", "temporal compliance requirement" }, 
	returnLabels = { "Temporal Pattern", "Initial Marking", "Final Marking", "temporal compliance requirement" },
	returnTypes = { PetriNetWithData.class, Marking.class, Marking.class, TemporalComplianceRequirement.class },
	help = "Generate a temporal pattern for compliance checking from parameters.", userAccessible = true)
public class CreateTemporalPattern_Plugin {
	

	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Create Temporal Pattern for Compliance Checking", requiredParameterLabels = { 0 })
	public Object[] createTemporalPattern(UIPluginContext context, XLog log) {
		
		TemporalComplianceRequirement req = new TemporalComplianceRequirement();
		CreateTemporalPattern_UI ui_temporal_req = new CreateTemporalPattern_UI(log, req);
		if (ui_temporal_req.setParameters(context, req) == InteractionResult.CANCEL) {
			return cancel(context, "Cancelled by user.");
		}
		
		EnrichLog_UI ui_fallback = new EnrichLog_UI(req);
		if (ui_fallback.setParameters(context, req) != InteractionResult.CANCEL) {
			return createTemporalPattern(context, log, req);			
		} else {
			return cancel(context, "Cancelled by user.");
		}
	}

	public static final String T_START = "Start";
	public static final String T_END = "End";
	public static final String T_INSTANCE_START = AbstractLogForCompliance_Plugin.INSTANCE_START_TRANSITION;
	public static final String T_INSTANCE_COMPLETE = AbstractLogForCompliance_Plugin.INSTANCE_COMPLETE_TRANSITION;
	public static final String T_OTHER = "other";
	
	/**
	 * @param name
	 * @return true iff the given activity name of the temporal pattern is a predefined name and not a parameter of the pattern
	 */
	public static boolean isPredefinedName(String name) {
		return 	   name.equals(CreateTemporalPattern_Plugin.T_START)
				|| name.equals(CreateTemporalPattern_Plugin.T_END)
				|| name.equals(CreateTemporalPattern_Plugin.T_INSTANCE_START)
				|| name.equals(CreateTemporalPattern_Plugin.T_INSTANCE_COMPLETE)
				|| name.equals(CreateTemporalPattern_Plugin.T_OTHER);
	}
	
	@PluginVariant(variantLabel = "Create Temporal Pattern for Compliance Checking", requiredParameterLabels = { 0, 1 })
	public Object[] createTemporalPattern(PluginContext context, XLog log, TemporalComplianceRequirement req) {
		
		// step 1: create low-level Petri net skeleton of temporal pattern
		Petrinet net = PetrinetFactory.newPetrinet("temporal pattern skeleton");
		
		Map<String, Transition> transitions = new HashMap<String, Transition>();
		
		Place p_initial = net.addPlace("initial");
		Place p_inactive = net.addPlace("inactive");
		Place p_active = net.addPlace("active");
		Place p_final = net.addPlace("final");
		
		Transition t_start = net.addTransition(T_START);
		net.addArc(p_initial, t_start);
		net.addArc(t_start, p_inactive);
		transitions.put(T_START, t_start);
		
		Transition t_end = net.addTransition(T_END);
		net.addArc(p_inactive, t_end);
		net.addArc(t_end, p_final);
		transitions.put(T_END, t_end);
		
		Transition t_I_start = net.addTransition(T_INSTANCE_START);
		net.addArc(p_inactive, t_I_start);
		net.addArc(t_I_start, p_active);
		transitions.put(T_INSTANCE_START, t_I_start);
		
		Transition t_I_complete = net.addTransition(T_INSTANCE_COMPLETE);
		net.addArc(p_active, t_I_complete);
		net.addArc(t_I_complete, p_inactive);
		transitions.put(T_INSTANCE_COMPLETE, t_I_complete);
		
		Transition t_omega_1 = net.addTransition(T_OTHER);
		net.addArc(p_inactive, t_omega_1);
		net.addArc(t_omega_1, p_inactive);
		Transition t_omega_2 = net.addTransition(T_OTHER);
		net.addArc(p_active, t_omega_2);
		net.addArc(t_omega_2, p_active);

		// create 1 transition for each specified event

		for (String name : req.patterNameToEventID.keySet()) {
			if (isPredefinedName(name)) continue;
			Transition t_x = net.addTransition(req.patterNameToEventID.get(name));
			net.addArc(p_active, t_x);
			net.addArc(t_x, p_active);
			
			transitions.put(name, t_x);
		}
		
		// step2: migrate low-level net to Petri net with variables
		PetriNetWithDataFactory f = new PetriNetWithDataFactory(net, "temporal pattern"); 
		PetriNetWithData temporal_net = f.getRetValue();
		
		// update mapping of event names to transitions to the new DataPetriNet transitions
		for (String name : req.patterNameToEventID.keySet()) {
			transitions.put(name, f.getTransMapping().get(transitions.get(name)));
		}
		
		// step 3: create initial and final marking
		Marking m_initial = new Marking();
		m_initial.add(f.getPlaceMapping().get(p_initial));
		context.addConnection(new InitialMarkingConnection(temporal_net, m_initial));
		
		Marking m_final = new Marking();
		m_final.add(f.getPlaceMapping().get(p_final));
		context.addConnection(new FinalMarkingConnection(temporal_net, m_final));

		// step 4: declare variables
		Map<String, String> patternNameToVariableName = new HashMap<String, String>();
		Map<String, DataElement> declaredVariables = new HashMap<String, DataElement>();
		for (String name : req.patterNameToEventID.keySet()) {
			if (isPredefinedName(name)) continue;
			
			String varName = req.patterNameToEventID.get(name);
			varName = varName.replace('+', '_');
			varName = "var_"+varName;
			
			DataElement var = temporal_net.addVariable(varName, java.util.Date.class, null, null);
			patternNameToVariableName.put(name, varName);
			declaredVariables.put(varName, var);

			temporal_net.assignWriteOperation(transitions.get(name), var);
		}
		
		// step 5: write guards and set read and write operations on transitions
		try {
			
			// for each guard
			for (String name : req.guards.keySet()) {
				String guard_string = req.guards.get(name);
				
				// first guard each pattern variable name with two special characters
				for (String pattern_var_name : req.patterNameToEventID.keySet()) {
					if (patternNameToVariableName.get(pattern_var_name) == null) continue;
					guard_string = guard_string.replace(pattern_var_name, "$"+pattern_var_name+"$");
				}
				// then replace each guard string with its actual definitions
				for (String pattern_var_name : req.patterNameToEventID.keySet()) {
					String actualVariable = patternNameToVariableName.get(pattern_var_name);
					if (actualVariable == null) continue;
					guard_string = guard_string.replace("$"+pattern_var_name+"$", actualVariable);
				}
				
				System.out.println("guard: "+guard_string);
				
				Expression guard = new Expression(guard_string);
				
				// get list of read and written variables
				List<String> readVars = TemporalComplianceRequirement.getReadVars(guard_string);
				List<String> writeVars = TemporalComplianceRequirement.getWriteVars(guard_string);
				
				// set read and write operations to transition
				for (String v : readVars) {
					temporal_net.assignReadOperation(transitions.get(name), declaredVariables.get(v));
				}
				for (String v : writeVars) {
					temporal_net.assignWriteOperation(transitions.get(name), declaredVariables.get(v));
				}
				
				// and set guard
				temporal_net.setGuard(transitions.get(name), guard);
			}
			
			// for each event time-stamp initialization specification
			for (TemporalComplianceRequirement.EventTimeStampInit ts_init : req.eventTimeStampInitSpec) {
				String initVar = patternNameToVariableName.get(ts_init.activity_initFor);
				System.out.println(ts_init.activity_initBy+" initializes "+initVar);
				temporal_net.assignWriteOperation(transitions.get(ts_init.activity_initBy), declaredVariables.get(initVar));
			}
			
		} catch (Exception e) {
			System.err.println(e);
			e.printStackTrace();
		}
	
		return new Object[] { temporal_net, m_initial, m_final, req };
	}
	

	
	protected static Object[] cancel(PluginContext context, String message) {
		System.out.println("[Temporal Compliance]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		context.getFutureResult(1).cancel(true);
		context.getFutureResult(2).cancel(true);
		context.getFutureResult(3).cancel(true);
		return null;
	}
}
