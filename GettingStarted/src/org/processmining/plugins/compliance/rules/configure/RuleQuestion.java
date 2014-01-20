package org.processmining.plugins.compliance.rules.configure;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.plugins.compliance.rules.select.constraints.Expression;

public class RuleQuestion extends GuideElement {
	
	private List<RuleAnswer> 	answers = new LinkedList<RuleAnswer>();
	private Map<String, Integer> parameters = new HashMap<String, Integer>();
	private Map<String, String> inheritedParameters = new HashMap<String, String>();

	/**
	 * 
	 * @param givenConfiguration
	 * @return true iff the configuration required by the question is met by the given configuration
	 */
	public boolean isEnabled(Map<String, Configuration> givenConfiguration) {
		System.out.println("checking "+getName()+":"+getText());
		for (Configuration requiredConfig : getConfigurations()) {
			
			Configuration givenConfig = givenConfiguration.get(requiredConfig.getFeatureGroupId());
			for (String parameter : requiredConfig.keySet()) {
				
				if (requiredConfig.get(parameter) == GuideElement.PROPERTY_NOT_SET) {
					// parameter must not be set yet, but is already set, question is not enabled
					if (givenConfig.containsKey(parameter)) {
						System.out.println("not enabled: "+parameter+" is set in config with "+givenConfig.get(parameter)+", but must not be set (yet)");
						return false;
					}
				} else {
					// parameter must be set, check for same value
					if (!givenConfig.containsKey(parameter)) {
						System.out.println("not enabled: "+parameter+" not set in config, but required: "+requiredConfig.get(parameter));
						return false;
					}
					// if the requirement is not just "has been set", then check for equality of values
					if (requiredConfig.get(parameter) != GuideElement.PROPERTY_IS_SET) {
						if (!givenConfig.get(parameter).equals(requiredConfig.get(parameter))) {
							System.out.println("not enabled: "+parameter+" = "+givenConfig.get(parameter)+", expected: "+requiredConfig.get(parameter));
							return false;
						}
					}
				}
			}
			// each parameter of the required configuration matches the given configuration 
		} // each required configuration matches the given configuration
		
		return true;
	}
	
	public boolean isEnabledForParameters(Map<String, Integer> parameters) throws ParseException {
		Hashtable<String, Integer> values = new Hashtable<String, Integer>();
		for (String p : parameters.keySet()) values.put(p, parameters.get(p));
		
		for (String p : inheritedParameters.keySet()) {
			if (inheritedParameters.get(p).equalsIgnoreCase(GuideElement.PROPERTY_IS_SET)) {
				if (!values.containsKey(p)) return false;
			} else {
				Expression exp = new Expression(inheritedParameters.get(p));
				if (exp.isFalse(values)) return false;
			}
		}
		return true;
	}
	
	public RuleQuestion(int num) {
		super("question #"+num);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("  question: "+getText()+"\n");
		sb.append("    has parameters: "+parameters+"\n");
		sb.append("    inherited parameters: "+inheritedParameters+"\n");
		sb.append("    prerequisite\n");
		for (Configuration c : getConfigurations()) {
			sb.append("    "+c.getFeatureGroupId()+"\n");
			for (String key : c.keySet()) {
				sb.append("        "+key+"="+c.get(key)+"\n");
			}
		}
		for (RuleAnswer a : answers) {
			sb.append(a.toString());
		}
		return sb.toString();
	}

	public void addAnswer(RuleAnswer answer) {
		answers.add(answer);
	}
	
	public List<RuleAnswer> getAnswers() {
		return answers;
	}
	
	public Map<String, Integer> getParameters() {
		return parameters;
	}
	
	public Map<String, String> getInheritedParameters() {
		return inheritedParameters;
	}

	public void isValidFor(ConfigurablePetrinet<?> net, List<String> errors) {
		
		isConfigurationListValidForNet(net, errors);
		for (RuleAnswer answer : answers) {
			answer.isValidFor(net, errors);
		}
	}
}
