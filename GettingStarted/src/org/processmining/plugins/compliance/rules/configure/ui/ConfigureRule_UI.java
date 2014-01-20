package org.processmining.plugins.compliance.rules.configure.ui;

import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.processmining.plugins.compliance.rules.configure.ConfigureRule_Plugin.QA_Configuration;
import org.processmining.plugins.compliance.rules.configure.RuleQuestion;
import org.processmining.plugins.compliance.rules.ui.widgets.UIUtils;

public class ConfigureRule_UI extends org.processmining.plugins.compliance.rules.ui.widgets.ProMWizardPanel<QA_Configuration, QA_Configuration> {
	
	private static final long serialVersionUID = 1L;
	public static final String DIALOG_TITLE = "Configure Compliance Rule";
	
	private JPanel scrollPanel;
	
	public ConfigureRule_UI() {
		super();
		scrollPanel = UIUtils.addScrollPanelToContainer(this);
	}

	private List<RuleQuestion_UI> questions_ui = new LinkedList<RuleQuestion_UI>();
	private ButtonGroup answerGroup;
	
	protected void initializeUIFromParameters(QA_Configuration input) {
		questions_ui.clear();
		scrollPanel.removeAll();
		answerGroup = new ButtonGroup();
		
		for (RuleQuestion question : input.enabledQuestions) {
			RuleQuestion_UI question_ui = new RuleQuestion_UI(input);
			question_ui.setValues(question);
			
			questions_ui.add(question_ui);
			scrollPanel.add(question_ui.getPanel());
			scrollPanel.add(Box.createVerticalStrut(5));
			
			for (JRadioButton b : question_ui.getAnswerButtons()) {
				answerGroup.add(b);
			}
		}
	}

	protected void getParametersFromUI(QA_Configuration output) throws Exception {
		output.enabledQuestions.clear();
		for (RuleQuestion_UI question_ui : questions_ui) {
			QA_Configuration config = question_ui.getConfigured();
			if (config != null) {
				output.update(config); 
			}
		}
	}

	protected String getTitle() {
		return DIALOG_TITLE;
	}
	
	
	
}