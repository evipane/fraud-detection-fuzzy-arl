package org.processmining.plugins.compliance.temporal;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.processmining.models.guards.Expression;

/**
 * Specifies a temporal compliance requirement that can be expressed in terms of
 * the general temporal compliance pattern Petri net by Ramezani et al.
 * 
 * @author dfahland
 * 
 */
public class TemporalComplianceRequirement {
	/**
	 * Specifies which activities the pattern has, and to which event names each
	 * activity is mapped. Each activity A is assumed to record the timestamp of
	 * its last occurrence in a variable A. This variable can be used in
	 * {@link #guards} to constrain occurrences of activities.
	 */
	public Map<String, String> patterNameToEventID = new HashMap<String, String>();
	
	/**
	 * Guards of activities in a temporal compliance pattern. A guard can be any
	 * string that is parseable by {@link Expression}. The literals in a guard
	 * are activity names used in {@link #patterNameToEventID} as well as
	 * {@value CreateTemporalPattern_Plugin#T_START},
	 * {@value CreateTemporalPattern_Plugin#T_END},
	 * {@value CreateTemporalPattern_Plugin#T_INSTANCE_START}, and
	 * {@value CreateTemporalPattern_Plugin#T_INSTANCE_COMPLETE}. The guard of
	 * activity A can additionally contain the literal A' referring to the
	 * current timestamp of activity A, when A is executed, whereas literal A
	 * just refers to the last time A was executed.
	 */
	public Map<String, String> guards = new HashMap<String, String>();
	
	/**
	 * Specifies for an event of class {@link #initFor}, which preceding event
	 * should provide the time information for that event in case there is no
	 * event of class {@link #initFor} or in case it lacks a valid timestamp.
	 * 
	 * The event class providing time information is specified by field
	 * {@link #initBy}. If the field is {@code null}, any class different
	 * from {@link #initFor} can provide the time information.
	 * 
	 * @author dfahland
	 * 
	 */
	public static class EventTimeStampInit {
		public XEventClass initFor;
		public XEventClass initBy;
		public long offset = 0;
		
		public String activity_initFor;
		public String activity_initBy;
	}
	
	public XEventClasses 				allEventClasses;
	public List<EventTimeStampInit> 	eventTimeStampInitSpec = new LinkedList<EventTimeStampInit>();

	/**
	 * @return all variables occurring in some guard, these variables are
	 *         identical to the transition names of the temporal compliance
	 *         requirement
	 */
	public Set<String> getGuardEvents() {
		Set<String> allReadVars = new TreeSet<String>();
		for (String guard : guards.values()) {
			allReadVars.addAll(getReadVars(guard));
		}
		return allReadVars;
	}
	
	/**
	 * @param guard_string
	 * @return all variables occurring in the guard
	 */
	@SuppressWarnings({ "cast", "unchecked" })
	public static Enumeration<String> getVariables(String guard_string) {
		Expression guard;
		try {
			guard = new Expression(guard_string);
			return (Enumeration<String>)guard.findVariables();
		} catch (java.text.ParseException e) {
			return null;
		}
	}

	/**
	 * @param guard_string
	 * @return all variables that are read in the guard
	 */
	public static List<String> getReadVars(String guard_string) {
		List<String> readVars = new LinkedList<String>();
		Enumeration<String> vars = getVariables(guard_string);
		if (vars != null) {
			while (vars.hasMoreElements()) {
				String v = vars.nextElement();
				// a read variable has has the form <name> and no trailing '
				if (v.lastIndexOf('\'') != v.length()-1) {
					readVars.add(v);
				}
			}
		}
		return readVars;
	}
	
	/**
	 * @param guard_string
	 * @return all variables that are written in the guard
	 */
	public static List<String> getWriteVars(String guard_string) {
		List<String> writeVars = new LinkedList<String>();
		Enumeration<String> vars = getVariables(guard_string);
		if (vars != null) {
			while (vars.hasMoreElements()) {
				String v = vars.nextElement();
				// a written variable has the form <name>'
				if (v.lastIndexOf('\'') == v.length()-1) {
					String vName = v.substring(0, v.length()-1); // strip prime from variable
					writeVars.add(vName);
				}
			}
		}
		return writeVars;
	}
}
