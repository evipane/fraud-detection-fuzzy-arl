package org.processmining.plugins.compliance.rules.configure;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.compliance.rules.elicit.util.SampleTraces;

public class RuleAnswer extends GuideElement {
	
	private List<List<String>>			sample_compliant = new LinkedList<List<String>>();
	private List<List<String>>			sample_violating = new LinkedList<List<String>>();
	
	private String changedModelName;
	
	public RuleAnswer(RuleQuestion parent, int num) {
		super("answer #"+num+" of "+parent.getName());
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("    answer: "+getText()+"\n");
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
		
		// expand parameterized traces before checking validity
		List<List<String>> sample_compliant_exp = new LinkedList<List<String>>();
		for (List<String> trace : sample_compliant) {
			List<String> t= SampleTraces.expandTraceByParameter(trace, null);
			System.out.println(trace +"  -->  "+t);
			sample_compliant_exp.add(t);
		}
		for (List<String> trace : sample_compliant_exp) {
			if (!isTraceValidForNet(trace, net)) {
				errors.add("compliant trace "+trace+" is not valid in "+getName());
			}
		}

		// expand parameterized traces before checking validity
		List<List<String>> sample_violating_exp = new LinkedList<List<String>>();
		for (List<String> trace : sample_violating) {
			List<String> t= SampleTraces.expandTraceByParameter(trace, null);
			System.out.println(trace +"  -->  "+t);
			sample_violating_exp.add(t);
		}
		for (List<String> trace : sample_violating_exp) {
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

	public String getChangedModelName() {
		return changedModelName;
	}

	public void setChangedModelName(String changedModelName) {
		this.changedModelName = changedModelName;
	}

	public boolean isEnabledForParameters(Map<String, Integer> parameterMapping) {
		return true;
	}

}
