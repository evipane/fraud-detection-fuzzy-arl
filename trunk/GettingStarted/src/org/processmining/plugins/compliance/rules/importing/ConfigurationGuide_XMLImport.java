package org.processmining.plugins.compliance.rules.importing;

import java.util.ArrayList;
import java.util.List;

import org.processmining.models.graphbased.directed.petrinet.configurable.Configuration;
import org.processmining.models.graphbased.directed.petrinet.configurable.impl.ConfigurableGraphElementOption;
import org.processmining.plugins.compliance.rules.configure.ConfigurationGuide;
import org.processmining.plugins.compliance.rules.configure.GuideElement;
import org.processmining.plugins.compliance.rules.configure.RuleAnswer;
import org.processmining.plugins.compliance.rules.configure.RuleQuestion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigurationGuide_XMLImport {
	
	private boolean visit_hasActivity(Element e, ConfigurationGuide pattern) {
		if ("hasActivity".equals(e.getNodeName())) {
			String name = e.getAttribute("name");
			pattern.getActivityNames().add(name);
			return true;
		}
		return false;
	}

	public ConfigurationGuide visit_document(Document doc) {
		Node root = doc.getFirstChild();
		if ("configurablePattern".equals(root.getNodeName())) {
			ConfigurationGuide pattern = new ConfigurationGuide();
			
			NodeList c = root.getChildNodes();
			for (int i=0; i<c.getLength(); i++) {
				if (c.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element n = (Element)c.item(i);
					
					visit_hasActivity(n, pattern);
					visit_question(n, pattern);
				}
			}
			
			return pattern;
		} else {
			return null;
		}
	}

	
	private void visit_group(Element e, List<Configuration> configs, boolean allowNotSet) {
		NodeList groups = e.getElementsByTagName("group");
		for (int i=0; i<groups.getLength(); i++) {
			Element group = (Element)groups.item(i);
			
			Configuration config = new Configuration(group.getAttribute("id"));
			
			NodeList properties = group.getElementsByTagName("property");
			for (int j=0; j<properties.getLength(); j++) {
				Element property = (Element)properties.item(j);
				
				String name = property.getAttribute("name");
				if (allowNotSet && property.hasAttribute("isSet"))
				{
					if (property.getAttribute("isSet").equalsIgnoreCase("false")) {
						config.put(name, GuideElement.PROPERTY_NOT_SET);
					} else if (property.getAttribute("isSet").equalsIgnoreCase("true")) {
						config.put(name, GuideElement.PROPERTY_IS_SET);
					}
				}
				else
				{
					String valueStr = property.getAttribute("value"); 
					Object value = getTypedValueFromString(valueStr);
					System.out.println("putting "+name+"="+value);
					config.put(name, value);
				}
			}
			
			configs.add(config);
		}
		
	}
	
	private void visit_preRequisite(Element e, RuleQuestion question) {
		if ("preconfiguration".equals(e.getNodeName())) {
			visit_group(e, question.getConfigurations(), true);
		}
	}
	
	private void visit_hasParameter(Element e, RuleQuestion question) {
		if ("hasParameter".equals(e.getNodeName())) {
			String name = e.getAttribute("name");
			String defaultString = e.getAttribute("default");
			int defaultValue = 0;
			try {
				defaultValue = Integer.parseInt(defaultString);
			} catch (NumberFormatException ex) {
			}
			question.getParameters().put(name, defaultValue);
		}
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
	
	private void visit_question(Element e, ConfigurationGuide pattern) {
		if ("question".equals(e.getNodeName())) {
			
			RuleQuestion question = new RuleQuestion(pattern.getQuestions().size());
			question.setText(e.getAttribute("text"));
			
			NodeList c = e.getChildNodes();
			for (int i=0; i<c.getLength(); i++) {
				if (c.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element n = (Element)c.item(i);
		
					visit_hasParameter(n, question);
					visit_inheritedParameter(n, question);
					visit_preRequisite(n, question);
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
	
	private void visit_configuration(Element e, RuleAnswer answer) {
		if ("configuration".equals(e.getNodeName())) {
			visit_group(e, answer.getConfigurations(), false);
		}
	}
	
	private void visit_configModel(Element e, RuleAnswer answer) {
		if ("configModel".equals(e.getNodeName())) {
			
			String changedModelName = e.getAttribute("name");
			answer.setChangedModelName(changedModelName);

			NodeList c = e.getChildNodes();
			for (int i=0; i<c.getLength(); i++) {
				if (c.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element n = (Element)c.item(i);
					
					visit_configuration(n, answer);
				}
			}
		}
	}
	
	private void visit_answer(Element e, RuleQuestion question) {
		
		if ("answer".equals(e.getNodeName())) {
			
			RuleAnswer answer = new RuleAnswer(question, question.getAnswers().size());
			answer.setText(e.getAttribute("text"));
			
			NodeList c = e.getChildNodes();
			for (int i=0; i<c.getLength(); i++) {
				if (c.item(i).getNodeType() == Node.ELEMENT_NODE) {
					Element n = (Element)c.item(i);
					
					visit_configModel(n, answer);
					visit_configuration(n, answer);
					visit_sampletrace(n, answer);
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
		
		if (Character.isJavaIdentifierStart(value.charAt(0))) {
			boolean identifier = true;
			for (int i=1;i<value.length();i++) {
				if (!Character.isJavaIdentifierPart(value.charAt(i))) {
					identifier = false;
					break;
				}
			}
			if (identifier) return value;
		}
		
		return null;
	}

}
