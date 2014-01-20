package org.processmining.plugins.compliance.rules.select;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.compliance.rules.configure.GuideElement;
import org.processmining.plugins.compliance.rules.select.constraints.Expression;

public class RuleAnswer extends GuideElement {
	
	private List<List<String>>			sample_compliant = new LinkedList<List<String>>();
	private List<List<String>>			sample_violating = new LinkedList<List<String>>();
	
	private List<String> followUpQuestionIDs = new LinkedList<String>();
	
	private List<Expression> parameterConstraints = new LinkedList<Expression>();
	private Map<String, String> parametersToSet = new HashMap<String, String>();
	
	private String configuredModel;
	
	public RuleAnswer(RuleQuestion parent, int num) {
		super("answer #"+num+" of question "+parent.getId());
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("    answer: "+getText()+"\n");
		sb.append("      parameter constraints: "+parameterConstraints+"\n");
		sb.append("      follow-up: "+followUpQuestionIDs+"\n");
		sb.append("      model: "+configuredModel+"\n");
		sb.append("      configurations\n");
		for (Configuration c : getConfigurations()) {
			sb.append("        "+c.getFeatureGroupId()+"\n");
			for (String key : c.keySet()) {
				sb.append("          "+key+"="+c.get(key)+"\n");
			}
		}
		for (List<String> tr : sample_compliant) {
			sb.append("      compliant: "+tr+"\n");
		}
		for (List<String> tr : sample_violating) {
			sb.append("      violating: "+tr+"\n");
		}
		return sb.toString();
	}
	
	public void addCompliantTrace(List<String> trace) {
		sample_compliant.add(trace);
	}
	
	public void addViolatingTrace(List<String> trace) {
		sample_violating.add(trace);
	}
	
	public boolean isEnabledForParameters(Map<String, Integer> parameters) {
		Hashtable<String, Integer> values = new Hashtable<String, Integer>();
		for (String p : parameters.keySet()) values.put(p, parameters.get(p));
		for (Expression constraint : parameterConstraints) {
			if (constraint.isFalse(values)) return false;
		}
		return true;
	}
	
	private static boolean isTraceValidForNet(List<String> trace, ConfigurablePetrinet<?> net) {
		for (String event : trace) {
			boolean event_found = false;
			for (Transition t : net.getTransitions()) {
				if (t.getLabel().equals(event)) {
					event_found = true;
					break;
				}
			}
			if (!event_found) return false;
		}
		return true;
	}
	
	public void isValidFor(ConfigurablePetrinet<?> net, List<String> errors) {
		isConfigurationListValidForNet(net, errors);
		for (List<String> trace : sample_compliant) {
			if (!isTraceValidForNet(trace, net)) {
				errors.add("compliant trace "+trace+" is not valid in "+getName());
			}
		}
		for (List<String> trace : sample_violating) {
			if (!isTraceValidForNet(trace, net)) {
				errors.add("violating trace "+trace+" is not valid in "+getName());
			}
		}
	}

	public List<List<String>> getCompliantTraces() {
		return sample_compliant;
	}
	
	public List<List<String>> getViolatingTraces() {
		return sample_violating;
	}

	public List<String> getFollowUpQuestionID() {
		return followUpQuestionIDs;
	}
	
	public List<Expression> getParameterConstraints() {
		return parameterConstraints;
	}
	
	public Map<String, String> getParametersToSet() {
		return parametersToSet;
	}

	public String getConfiguredModel() {
		return configuredModel;
	}

	public void setConfiguredModel(String configuredModel) {
		this.configuredModel = configuredModel;
	}


}
