package org.processmining.plugins.compliance.rules.importing;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.plugins.compliance.rules.select.RuleAnswer;
import org.processmining.plugins.compliance.rules.select.RuleQuestion;
import org.processmining.plugins.compliance.rules.select.RuleSelectGuide;
import org.processmining.plugins.compliance.rules.select.constraints.Expression;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RuleSelect_XMLImport {
	
	private boolean visit_hasActivity(Element e, RuleQuestion question) {
		if ("hasActivity".equals(e.getNodeName())) {
			String name = e.getAttribute("name");
			question.getActivityNames().add(name);
			return true;
		}
		return false;
	}
	
	private boolean visit_hasActivity(Element e, RuleSelectGuide guide) {
		if ("hasActivity".equals(e.getNodeName())) {
			String name = e.getAttribute("name");
			guide.getGlobalActivityNames().add(name);
			return true;
		}
		return false;
	}

	private boolean visit_hasParameter(Element e, RuleQuestion question) {
		if ("hasParameter".equals(e.getNodeName())) {
			String name = e.getAttribute("name");
			String defaultString = e.getAttribute("default");
			int defaultValue = 0;
			try {
				defaultValue = Integer.parseInt(defaultString);
			} catch (NumberFormatException ex) {
			}
			question.getParameters().put(name, defaultValue);
			return true;
		}
		return false;
	}
	
	private boolean visit_inheritedParameter(Element e, RuleQuestion question) {
		if ("inheritedParameter".equals(e.getNodeName())) {
			String name = e.getAttribute("name");
			String constraint = e.getAttribute("constraint");
			question.getInheritedParameters().put(name, constraint);
			return true;
		}
		return false;
	}
	
	private void visit_group(Element e, List<Configuration> configs) {
		NodeList groups = e.getElementsByTagName("group");
		for (int i=0; i<groups.getLength(); i++) {
			Element group = (Element)groups.item(i);
			
			Configuration config = new Configuration(group.getAttribute("id"));
			
			NodeList properties = group.getElementsByTagName("property");
			for (int j=0; j<properties.getLength(); j++) {
				Element property = (Element)properties.item(j);
				
				String name = property.getAttribute("name");
				String valueStr = property.getAttribute("value"); 
				Object value = getTypedValueFromString(valueStr);
				System.out.println("putting "+name+"="+value);
				config.put(name, value);
			}
			
			configs.add(config);
		}
		
	}
	
	private RuleQuestion currentQuestion = null;
	
	private void visit_question(Element e, RuleSelectGuide pattern) {
		if ("question".equals(e.getNodeName())) {
			
			RuleQuestion question = new RuleQuestion();
			currentQuestion = question;
			question.setText(e.getAttribute("text"));
			
			String id = e.getAttribute("id");
			if (id != null && id.length() > 0) question.setId(id);
			
			NodeList c = e.getChildNodes();
			for (int i=0; i<c.getLength(); i++) {
				if (c.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element n = (Element)c.item(i);
		
					visit_hasActivity(n, question);
					visit_hasParameter(n, question);
					visit_inheritedParameter(n, question);
					visit_answer(n, question);
				}
			}
			
			pattern.addQuestion(question);
		}
	}
	
	private void visit_sampletrace(Element e, RuleAnswer answer) {
		if ("sampletrace".equals(e.getNodeName())) {
			String trace = e.getTextContent();
			String events[] = trace.split(" ");
			
			ArrayList<String> trace2 = new ArrayList<String>();
			for (String event : events) {
				if (event.length() != 0) trace2.add(event);
			}
			
			if (e.getAttribute("compliant").equals("true")) {
				answer.addCompliantTrace(trace2);
			} else {
				answer.addViolatingTrace(trace2);
			}
		}
	}
	
	private void visit_followUpQuestionID(Element e, RuleAnswer answer) {
		if ("followUpQuestion".equals(e.getNodeName())) {
			String id = e.getAttribute("id");
			answer.getFollowUpQuestionID().add(id);
		}

	}
	
	private void visit_configuration(Element e, RuleAnswer answer) {
		if ("configuration".equals(e.getNodeName())) {
			visit_group(e, answer.getConfigurations());
		}
	}
	
	private void visit_configModel(Element e, RuleAnswer answer) {
		if ("configModel".equals(e.getNodeName())) {
			String name = e.getAttribute("name");
			answer.setConfiguredModel(name);
			
			NodeList c = e.getChildNodes();
			for (int i=0; i<c.getLength(); i++) {
				if (c.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element n = (Element)c.item(i);
		
					visit_configuration(n, answer);
				}
			}

		}
	}
	
	private boolean visit_parameter(Element e, RuleAnswer answer) {
		if ("parameter".equals(e.getNodeName())) {
			String constraint = e.getAttribute("constraint");
			
 			try {
				Expression exp = new Expression(constraint);
				answer.getParameterConstraints().add(exp);
			} catch (ParseException e1) {
				System.err.println("could not parse expression: "+constraint);
				e1.printStackTrace();
			}
		
			return true;
		}
		return false;
	}
	
	private boolean visit_setParameter(Element e, RuleAnswer answer) {
		if ("setParameter".equals(e.getNodeName())) {
			String name = e.getAttribute("name");
			String value = e.getAttribute("value");

			answer.getParametersToSet().put(name, value);
		
			return true;
		}
		return false;
	}
	
	private void visit_answer(Element e, RuleQuestion question) {
		
		if ("answer".equals(e.getNodeName())) {
			
			RuleAnswer answer = new RuleAnswer(question, question.getAnswers().size());
			answer.setText(e.getAttribute("text"));
			
			NodeList c = e.getChildNodes();
			for (int i=0; i<c.getLength(); i++) {
				if (c.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element n = (Element)c.item(i);
					
					visit_parameter(n, answer);
					visit_setParameter(n, answer);
					visit_sampletrace(n, answer);
					visit_followUpQuestionID(n, answer);
					visit_configModel(n, answer);
				}
			}
			question.addAnswer(answer);
		}
		
	}
	
	public Object getTypedValueFromString(String value) {
		if ("allow".equalsIgnoreCase(value)) return ConfigurableGraphElementOption.ALLOW;
		if ("block".equalsIgnoreCase(value)) return ConfigurableGraphElementOption.BLOCK;
		if ("skip".equalsIgnoreCase(value)) return ConfigurableGraphElementOption.SKIP;
		
		try {
			Integer i = Integer.parseInt(value);
			return i;
		} catch (NumberFormatException e) {
			// not a number, ignore
		}

		// a string, assume it is an expression, can only be checked at runtime
		// when all variables of the expression are set
		return value;
	}

	public RuleSelectGuide visit_document(Document doc) {
		Node root = doc.getFirstChild();
		if ("questions".equals(root.getNodeName())) {
			RuleSelectGuide pattern = new RuleSelectGuide();
			
			NodeList c = root.getChildNodes();
			for (int i=0; i<c.getLength(); i++) {
				if (c.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element n = (Element)c.item(i);
					
					visit_question(n, pattern);
					visit_hasActivity(n, pattern);
				}
			}

			return pattern;
		} else {
			return null;
		}
	}


}
