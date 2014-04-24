package org.processmining.pnml.model;

import java.util.List;

public class Transition {

	private String id;
	private String name;
	private String role;
	private String resource;
	private String coba;
	private String sebelum;
	private String sesudah;
	private Decision decision = new Decision();
	private List<Decision> decisions;
	private int time;
	
	
	public String getSebelum() {
		return sebelum;
	}
	public void setSebelum(String sebelum) {
		this.sebelum = sebelum;
	}
	public String getSesudah() {
		return sesudah;
	}
	public void setSesudah(String sesudah) {
		this.sesudah = sesudah;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	public String getResource() {
		return resource;
	}
	public void setResource(String resource) {
		this.resource = resource;
	}
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	
	public void setDecision(String nextTransition, String attribute, String ruleDecision) {
		this.decision.setNextTransition(nextTransition);
		this.decision.setAttribute(attribute);
		this.decision.setRuleDecision(ruleDecision);
		this.decisions.add(decision);
	}
	
	public List<Decision> getDecision() {
		return decisions;
	}
	public void setCoba(String coba) {
		this.coba = coba;
	}
	public String getCoba() {
		return coba;
	}
	
}
