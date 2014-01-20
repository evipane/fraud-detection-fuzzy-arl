package org.processmining.plugins.compliance.temporal;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

public class ComplianceDiagnostics implements org.processmining.framework.util.HTMLToString {

	public Map<Object, Integer> resourceInCase = new HashMap<Object, Integer>();
	public Map<Object, Integer> resourceInViolatingCase = new HashMap<Object, Integer>();
	
	public Map<Object, Integer> mostImportantResourceInCase = new HashMap<Object, Integer>();
	public Map<Object, Integer> mostImportantResourceInViolatedCase = new HashMap<Object, Integer>();
	
	public Map<Integer, Integer> casesPerResourceNumInCase = new HashMap<Integer, Integer>();
	public Map<Integer, Integer> violationsPerResourceNumInCase = new HashMap<Integer, Integer>();
	public Map<Integer, Integer> casesPerHandoversInCase = new HashMap<Integer, Integer>();
	public Map<Integer, Integer> violationsPerHandoversInCase = new HashMap<Integer, Integer>();
	
	public String toHTMLString(boolean includeHTMLTags) {
		StringBuilder sb = new StringBuilder();
		
		if (includeHTMLTags) sb.append("<html>");
		
		sb.append("<h1>Compliance Statistics</h1>");
		sb.append("<table>");
		sb.append("  <tr><td>Resource</td><td># cases</td><td># violating Cases</td><td>% violating Cases</td><td># cases (r was most important)</td><td># violating cases (r was most important)</td><td>% violating most important</td></tr>");
		for (Object r : resourceInCase.keySet()) {
			int cases = (resourceInCase.containsKey(r)) ? resourceInCase.get(r) : 0;
			int violations = (resourceInViolatingCase.containsKey(r)) ? resourceInViolatingCase.get(r) : 0;
			double fraction = (cases == 0) ? 0 : (double)violations/cases;
			
			int mostImportantCases = (mostImportantResourceInCase.containsKey(r)) ? mostImportantResourceInCase.get(r) : 0;
			int  mostImportantViolations = (mostImportantResourceInViolatedCase.containsKey(r)) ? mostImportantResourceInViolatedCase.get(r) : 0;
			double fractionMostImportant = (mostImportantCases == 0) ? 0 : (double)mostImportantViolations/mostImportantCases;
			
			sb.append("<tr>");
			sb.append("<td>"+r.toString()+"</td><td>"+cases+"</td><td>"+violations+"</td><td>"+fraction+"</td>");
			sb.append("<td>"+mostImportantCases+"</td><td>"+mostImportantViolations+"</td><td>"+fractionMostImportant+"</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("<hr />");
		
		TreeSet<Integer> handovers = new TreeSet<Integer>(casesPerHandoversInCase.keySet());
		sb.append("<table>");
		sb.append("  <tr><td># handovers</td><td># total cases</td><td># violated cases</td><td>% violated cases</td></tr>");
		for (Integer handover : handovers) {
			int cases = casesPerHandoversInCase.get(handover);
			int violations = (violationsPerHandoversInCase.containsKey(handover)) ? violationsPerHandoversInCase.get(handover) : 0;
			double fraction = (cases == 0) ? 0 : (double)violations/cases;
			
			sb.append("<tr>");
			sb.append("<td>"+handover+"</td><td>"+cases+"</td><td>"+violations+"</td><td>"+fraction+"</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("<hr />");
		
		TreeSet<Integer> resources = new TreeSet<Integer>(casesPerResourceNumInCase.keySet());
		sb.append("<table>");
		sb.append("  <tr><td># resources in case</td><td># total cases</td><td># violated cases</td><td>% violated cases</td></tr>");
		for (Integer res_num : resources) {
			int cases = casesPerResourceNumInCase.get(res_num);
			int violations = (violationsPerResourceNumInCase.containsKey(res_num)) ? violationsPerResourceNumInCase.get(res_num) : 0;
			double fraction = (cases == 0) ? 0 : (double)violations/cases;
			
			sb.append("<tr>");
			sb.append("<td>"+res_num+"</td><td>"+cases+"</td><td>"+violations+"</td><td>"+fraction+"</td>");
			sb.append("</tr>");
		}
		sb.append("</table>");
		sb.append("<hr />");
				
		if (includeHTMLTags) sb.append("</html>");
		
		return sb.toString();
	}

}
