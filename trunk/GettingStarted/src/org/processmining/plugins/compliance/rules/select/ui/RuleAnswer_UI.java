package org.processmining.plugins.compliance.rules.select.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.compliance.rules.elicit.ui.ProcessInstanceConformanceView;
import org.processmining.plugins.compliance.rules.elicit.ui.UIUtil;
import org.processmining.plugins.compliance.rules.elicit.util.SampleTraces;
import org.processmining.plugins.compliance.rules.select.RuleAnswer;
import org.processmining.plugins.compliance.rules.ui.widgets.Structured_UI;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class RuleAnswer_UI implements Structured_UI<RuleAnswer, Boolean>{
	
	private JPanel container;
	private JRadioButton button;
	private JLabel answerLabel;
	private RuleAnswer originalAnswer;
	
	private JPanel compliantTraces;
	private JPanel violatingTraces;
	
    public final static Color ANSWER_BACKGROUND = new Color(210, 210, 210);
	
	public RuleAnswer_UI() {
		container = SlickerFactory.instance().createRoundedPanel(10, ANSWER_BACKGROUND);
		container.setOpaque(false);
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
	}
	
	public JPanel getPanel() {
		return container;
	}
	
	public JRadioButton getRadioButton() {
		return button;
	}
	
	public RuleAnswer getOriginalAnswer() {
		return originalAnswer;
	}

	public void setValues(RuleAnswer input) {
		originalAnswer = input;
		
		SlickerFactory f = SlickerFactory.instance(); 
		
		container.removeAll();
		JPanel answerLine = new JPanel();
		answerLine.setOpaque(false);
		answerLine.setLayout(new BoxLayout(answerLine, BoxLayout.X_AXIS));
		
		button = f.createRadioButton("");
		answerLabel = f.createLabel("<html><p>"+input.getText()+"</p></html>");
		answerLabel.setMinimumSize(new Dimension(600,30));
		answerLabel.setMaximumSize(new Dimension(600,300));
		answerLine.add(button);
		answerLine.add(Box.createHorizontalStrut(5));
		answerLine.add(answerLabel);
		answerLine.add(Box.createHorizontalGlue());
		
		container.add(answerLine);
		
		JPanel traceLine = new JPanel();
		traceLine.setOpaque(false);
		traceLine.setLayout(new BoxLayout(traceLine, BoxLayout.X_AXIS));

		int height = Math.max(input.getCompliantTraces().size(), input.getViolatingTraces().size()) * ProcessInstanceConformanceView.PANEL_HEIGHT + 10;
		
		compliantTraces = f.createRoundedPanel(10, new Color(192, 210, 192));
		compliantTraces.setMinimumSize(new Dimension(200,30));
		compliantTraces.setPreferredSize(new Dimension(300,height));
		compliantTraces.setMaximumSize(new Dimension(400,1000));
		compliantTraces.setLayout(new BoxLayout(compliantTraces, BoxLayout.Y_AXIS));
		for (List<String> trace : input.getCompliantTraces()) {
			//compliantTraces.add(f.createLabel(trace.toString()));
			compliantTraces.add(SampleTraces.getTraceVisualization(SampleTraces.getFromTrace(trace), "example", new HashMap<String, XEventClass>()));
		}
		
		violatingTraces = f.createRoundedPanel(10, new Color(210, 192, 192));
		violatingTraces.setMinimumSize(new Dimension(200,30));
		violatingTraces.setPreferredSize(new Dimension(300,height));
		violatingTraces.setMaximumSize(new Dimension(400,1000));
		violatingTraces.setLayout(new BoxLayout(violatingTraces, BoxLayout.Y_AXIS));
		for (List<String> trace : input.getViolatingTraces()) {
			//violatingTraces.add(f.createLabel(trace.toString()));
			violatingTraces.add(SampleTraces.getTraceVisualization(SampleTraces.getFromTrace(trace), "counter example", new HashMap<String, XEventClass>()));
		}
		
		traceLine.add(Box.createHorizontalGlue());
		traceLine.add(compliantTraces);
		traceLine.add(Box.createHorizontalStrut(5));
		traceLine.add(violatingTraces);
		traceLine.add(Box.createHorizontalGlue());
		
		container.add(traceLine);
	}
	
	public void refreshTraces(Map<String, XEventClass> activityMap, Map<String, Integer> parameterMap) {
		SlickerFactory f = SlickerFactory.instance(); 
		
		for (Component c : compliantTraces.getComponents()) { c.setVisible(false); c.setEnabled(false); }
		for (Component c : violatingTraces.getComponents()) { c.setVisible(false); c.setEnabled(false); }
		
		compliantTraces.removeAll();
		for (List<String> trace : originalAnswer.getCompliantTraces()) {
			List<String> iTrace = SampleTraces.instantiateTrace(trace, activityMap, parameterMap);
			//compliantTraces.add(f.createLabel(iTrace.toString()));
			compliantTraces.add(SampleTraces.getTraceVisualization(SampleTraces.getFromTrace(iTrace), "example", activityMap));
		}
		
		violatingTraces.removeAll();
		for (List<String> trace : originalAnswer.getViolatingTraces()) {
			List<String> iTrace = SampleTraces.instantiateTrace(trace, activityMap, parameterMap);
			//violatingTraces.add(f.createLabel(iTrace.toString()));
			violatingTraces.add(SampleTraces.getTraceVisualization(SampleTraces.getFromTrace(iTrace), "counter example", activityMap));
		}
		container.revalidate();
	}
	
	public void refreshText(Map<String, XEventClass> activityMap, Map<String, Integer> parameterMap) {
		String updatedAnswer = originalAnswer.getText();
		
		updatedAnswer = UIUtil.refreshText(updatedAnswer, activityMap, parameterMap);
		
		answerLabel.setText(updatedAnswer);
		container.revalidate();
	}

	public Boolean getConfigured() throws Exception {
		return button.isSelected();
	}

}
