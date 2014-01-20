package org.processmining.plugins.compliance.rules.configure;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.processmining.models.graphbased.directed.petrinet.configurable.ConfigurablePetrinet;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

public class ConfigurationGuide {
	
	
	
	private Set<String> activityNames = new HashSet<String>();
	
	private List<RuleQuestion> questions = new LinkedList<RuleQuestion>();
	
	public List<String> isValidFor(ConfigurablePetrinet<?> net, List<String> errors) {
		
		// each declared activity of the guide has to be matched to a transition
		for (String activity : getActivityNames()) {
			boolean activity_exists = false;
			for (Transition t : net.getTransitions()) {
				if (t.getLabel().equals(activity)) {
					activity_exists = true;
					break;
				}
			}
			// no transition labeled with activity: guide does not match the net
			if (!activity_exists) {
				errors.add("Unknown activitity "+activity);
			}
		}

		// each declared question has to match the net
		for (RuleQuestion question : questions) {
			question.isValidFor(net, errors);
		}
		return errors;
	}
	
	public List<RuleQuestion> getQuestions() {
		return questions;
	}

	public void addQuestion(RuleQuestion question) {
		questions.add(question);
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("configurablepattern\n");
		for (String a : getActivityNames()) {
			sb.append("  has activity: "+a+"\n");
		}
		for (RuleQuestion q : questions) {
			sb.append(q.toString());
		}
		return sb.toString();
	}

	public Set<String> getActivityNames() {
		return activityNames;
	}

}
