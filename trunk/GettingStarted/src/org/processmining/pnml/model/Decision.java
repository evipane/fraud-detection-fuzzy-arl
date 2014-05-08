package org.processmining.pnml.model;

public class Decision {
	private String firstTransition;
	private String nextTransition;
	private String attribute;
	private String typeAttribyte;
	private String ruleDecision;
	private String value;
	private String predicate;
	private String nextAttribute;
	
	public void setNextTransition(String nextTransition) {
		this.nextTransition = nextTransition;
	}
	public String getNextTransition() {
		return nextTransition;
	}
	public void setRuleDecision(String ruleDecision) {
		this.ruleDecision = ruleDecision;
	}
	public String getRuleDecision() {
		return ruleDecision;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setFirstTransition(String firstTransition) {
		this.firstTransition = firstTransition;
	}
	public String getFirstTransition() {
		return firstTransition;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
	public void setTypeAttribyte(String typeAttribyte) {
		this.typeAttribyte = typeAttribyte;
	}
	public String getTypeAttribyte() {
		return typeAttribyte;
	}
	public void setPredicate(String predicate) {
		this.predicate = predicate;
	}
	public String getPredicate() {
		return predicate;
	}
	public void setNextAttribute(String nextAttribute) {
		this.nextAttribute = nextAttribute;
	}
	public String getNextAttribute() {
		return nextAttribute;
	}
}
