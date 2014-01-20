package org.processmining.plugins.compliance.rules.configure.ui;

import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.plugins.compliance.rules.configure.ConfigureRule_Plugin.QA_Configuration;
import org.processmining.plugins.compliance.rules.configure.RuleAnswer;
import org.processmining.plugins.compliance.rules.configure.RuleQuestion;
import org.processmining.plugins.compliance.rules.elicit.ui.UIUtil;
import org.processmining.plugins.compliance.rules.ui.widgets.Structured_UI;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class RuleQuestion_UI implements Structured_UI<RuleQuestion, QA_Configuration> {
	
	private JPanel container;
	private JLabel questionText;
	private JPanel parametersPanel;
	private JPanel answersPanel;
	
	private String originalQuestionText;
	
	private Map<String, XEventClass> activityMapping = new HashMap<String, XEventClass>();
	private Map<String, Integer> parameterMapping = new HashMap<String, Integer>();
	
	private QA_Configuration contextConfiguration;
	
	public RuleQuestion_UI (QA_Configuration input) {
		
		contextConfiguration = input;
		
		SlickerFactory f = SlickerFactory.instance();
		
		container = f.createRoundedPanel(20, WidgetColors.PROPERTIES_BACKGROUND);
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		JPanel questionPanel = new JPanel();
		questionPanel.setOpaque(false);
		questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.X_AXIS));
		questionText = f.createLabel("question");
		questionPanel.add(questionText);
		questionPanel.add(Box.createHorizontalGlue());
		container.add(questionPanel);
		
		parametersPanel = new JPanel();
		parametersPanel.setOpaque(false);
		parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
		container.add(parametersPanel);
		container.add(Box.createVerticalStrut(5));
		
		answersPanel = new JPanel();
		answersPanel.setOpaque(false);
		answersPanel.setLayout(new BoxLayout(answersPanel, BoxLayout.Y_AXIS));
		container.add(answersPanel);
		
		activityMapping.putAll(input.ruleConfig.activityMapping);
		parameterMapping.putAll(input.ruleConfig.parameters);
	}
	
	public JPanel getPanel() {
		return container;
	}
	
	public List<JRadioButton> getAnswerButtons() {
		List<JRadioButton> buttons = new LinkedList<JRadioButton>();
		for (RuleAnswer_UI answer_ui : answers_ui) {
			buttons.add(answer_ui.getRadioButton());
		}
		return buttons;
	}

	private List<RuleAnswer_UI> answers_ui = new LinkedList<RuleAnswer_UI>();
	
	public void setValues(RuleQuestion input) {
		answers_ui.clear();
		answersPanel.removeAll();
		parametersPanel.removeAll();
		
		originalQuestionText = input.getText();
		questionText.setText("<html><h2>"+input.getText()+"</h2></html>");
		
		if (input.getParameters().size() > 0) {
			for (String param : input.getParameters().keySet()) {
				SlickerFactory f = SlickerFactory.instance();
				
				ProMTextField paramField = new ProMTextField(input.getParameters().get(param).toString());
				paramField.addFocusListener(new UpdateAnswerListener_Parameter(param, paramField));
				
				JPanel paramPanel = new JPanel();
				paramPanel.setOpaque(false);
				paramPanel.setLayout(new BoxLayout(paramPanel, BoxLayout.X_AXIS));
				paramPanel.setPreferredSize(new Dimension(800, 30));
				
				paramPanel.add(f.createLabel(param));
				paramPanel.add(Box.createHorizontalStrut(5));
				paramPanel.add(paramField);
				

				parametersPanel.add(paramPanel);
				parameterMapping.put(param, input.getParameters().get(param));
				
			}
		}
		
		for (RuleAnswer answer : input.getAnswers()) {
			RuleAnswer_UI answer_ui = new RuleAnswer_UI(contextConfiguration);
			answer_ui.setValues(answer);
			
			answers_ui.add(answer_ui);
			answersPanel.add(answer_ui.getPanel());
			answersPanel.add(Box.createVerticalStrut(1));
		}

		updatePanel();
	}
	
	private void updatePanel() {
		for (RuleAnswer_UI answer_ui : answers_ui) {
			answer_ui.refreshText(activityMapping, parameterMapping);
			answer_ui.refreshTraces(activityMapping, parameterMapping);
			
			if (answer_ui.getOriginalAnswer().isEnabledForParameters(parameterMapping)) {
				answer_ui.getPanel().setVisible(true);
			} else {
				answer_ui.getPanel().setVisible(false);
			}
				
		}
		
		String iQuestion = originalQuestionText;
		List<String> activities = new ArrayList<String>(activityMapping.keySet());
		Collections.sort(activities);
		for (int i=activities.size()-1;i>=0;i--) {
			String key = activities.get(i);
			iQuestion = UIUtil.insertActivity_colored(iQuestion, key, activityMapping.get(key).toString());
		}
		for (String key : parameterMapping.keySet()) {
			String dKey = "$"+key;
			iQuestion = iQuestion.replace(dKey, parameterMapping.get(key).toString());
		}
		questionText.setText("<html><h2>"+UIUtil.getWrappedLabel(iQuestion,70)+"</h2></html>");
		
		container.revalidate();
	}

	public QA_Configuration getConfigured() throws Exception {
		for (RuleAnswer_UI answer_ui : answers_ui) {
			if (answer_ui.getConfigured()) {
				QA_Configuration config = new QA_Configuration();
				config.chosenAnswer = answer_ui.getOriginalAnswer();
				config.ruleConfig.activityMapping.putAll(activityMapping);
				config.ruleConfig.parameters.putAll(parameterMapping);
				return config;
			}
		}
		return null;
	}
	
	private class UpdateAnswerListener_Parameter implements FocusListener {

		private String key;
		private ProMTextField choice;
		
		public UpdateAnswerListener_Parameter(String key, ProMTextField choice) {
			this.key = key;
			this.choice = choice;
		}

		public void focusGained(FocusEvent arg0) {
			setParameter();
		}

		public void focusLost(FocusEvent arg0) {
			setParameter();
		}
		
		private void setParameter() {
			try {
				parameterMapping.put(key, Integer.parseInt(choice.getText()));
				updatePanel();
			} catch (NumberFormatException e) {
			}
		}
	}
}
