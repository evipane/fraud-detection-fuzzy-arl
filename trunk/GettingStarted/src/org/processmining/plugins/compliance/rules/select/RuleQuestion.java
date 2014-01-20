package org.processmining.plugins.compliance.rules.select;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.processmining.plugins.compliance.rules.configure.GuideElement;

public class RuleQuestion {
	
	private String id;
	private List<RuleAnswer> 	answers = new LinkedList<RuleAnswer>();
	
	private Set<String> activityNames = new HashSet<String>();
	private Map<String, Integer> parameters = new HashMap<String, Integer>();
	private Map<String, String> inheritedParameters = new HashMap<String, String>();
	
	private String text;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("  question: "+getText()+" ("+getId()+")\n");
		sb.append("    has activities: "+activityNames+"\n");
		sb.append("    has parameters: "+parameters+"\n");
		sb.append("    inherited parameters: "+inheritedParameters+"\n");
		sb.append("    prerequisite\n");
		for (RuleAnswer a : answers) {
			sb.append(a.toString());
		}
		return sb.toString();
	}

	public boolean isEnabled(Map<String, Integer> parameters) {
		for (String par : inheritedParameters.keySet()) {
			String constraint = inheritedParameters.get(par);
			
			// check whether parameter has been set 
			if (constraint.equalsIgnoreCase(GuideElement.PROPERTY_IS_SET)) {
				if (!parameters.containsKey(par)) {
					System.err.println("Error. Parameter "+par+" is not set.");
					return false;
				} else {
					System.err.println("Parameter "+par+" = "+parameters.get(par));
				}
			}
		}
		return true;
	}
	
	public void addAnswer(RuleAnswer answer) {
		answers.add(answer);
	}
	
	public List<RuleAnswer> getAnswers() {
		return answers;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<String> getActivityNames() {
		return activityNames;
	}

	public Map<String, Integer> getParameters() {
		return parameters;
	}
	
	public Map<String, String> getInheritedParameters() {
		return inheritedParameters;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
