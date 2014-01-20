package org.processmining.plugins.compliance.rules.select;

import java.util.LinkedList;
import java.util.List;

public class RuleSelectGuide {
	
	private List<RuleQuestion> questions = new LinkedList<RuleQuestion>();
	private List<String> globalActivityNames = new LinkedList<String>();
	
	public List<RuleQuestion> getQuestions() {
		return questions;
	}

	public void addQuestion(RuleQuestion question) {
		questions.add(question);
	}
	
	public List<String> getGlobalActivityNames() {
		return globalActivityNames;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("global activities\n");
		sb.append(globalActivityNames+"\n");
		sb.append("questions\n");
		for (RuleQuestion q : questions) {
			sb.append(q.toString());
		}
		return sb.toString();
	}

}
