package org.processmining.plugins.compliance.rules.select.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.util.Pair;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.plugins.compliance.rules.ui.widgets.ProMWizardPanel;
import org.processmining.plugins.compliance.rules.ui.widgets.UIUtils;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class SelectActivities_UI extends ProMWizardPanel<XLog, Map<String,XEventClass>> {
	
	private static final long serialVersionUID = 1L;
	public static final String DIALOG_TITLE = "Which activities are constrained by the rule?";
	
	private JPanel scrollPanel;
	private ProMComboBox eventClassifierSelection;

	
	private JPanel eventPanelContainer;
	private List<SelectActivities_UI_EventPanel> eventPanels = new LinkedList<SelectActivities_UI_EventPanel>();
	
	private static final int EVENT_PANEL_WIDTH = 700;
	
	public SelectActivities_UI() {
		super();
		scrollPanel = UIUtils.addScrollPanelToContainer(this);
	}

	protected void initializeUIFromParameters(XLog input) {
		eventPanels.clear();
		scrollPanel.removeAll();
		this.log = input;
		
		SlickerFactory f = SlickerFactory.instance();
		
		JPanel eventClassPanel = new JPanel();
		eventClassPanel.setOpaque(false);
		eventClassPanel.setLayout(new BoxLayout(eventClassPanel, BoxLayout.X_AXIS));
		UIUtils.setFixedSize(eventClassPanel, new Dimension(EVENT_PANEL_WIDTH, 50));
		eventClassPanel.add(f.createLabel("event classifier"));
		eventClassPanel.add(Box.createHorizontalStrut(5));
		
		List<XEventClassifier> eventClassifiers = getEventClassifiers(input);
		eventClassifierSelection = new ProMComboBox(eventClassifiers);
		eventClassifierSelection.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateEventClassSelection();
			}
		});
		UIUtils.setFixedSize(eventClassifierSelection, new Dimension(EVENT_PANEL_WIDTH-100, 30));
		eventClassPanel.add(eventClassifierSelection);
		updateEventClassSelection();
		
		scrollPanel.add(eventClassPanel);

		eventPanelContainer = new JPanel();
		eventPanelContainer.setMinimumSize(new Dimension(EVENT_PANEL_WIDTH, 10));
		eventPanelContainer.setMaximumSize(new Dimension(EVENT_PANEL_WIDTH, 4000));
		eventPanelContainer.setOpaque(false);
		eventPanelContainer.setLayout(new BoxLayout(eventPanelContainer, BoxLayout.Y_AXIS));
		scrollPanel.add(eventPanelContainer);
		
		JPanel addEventsButtonPanel = new JPanel();
		addEventsButtonPanel.setOpaque(false);
		addEventsButtonPanel.setLayout(new BoxLayout(addEventsButtonPanel, BoxLayout.X_AXIS));
		UIUtils.setFixedSize(addEventsButtonPanel, new Dimension(EVENT_PANEL_WIDTH, 50));
		JButton addButton = UIUtils.createPlusButton();
		addButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				addNewEventPanel();
			}
		});
		addEventsButtonPanel.add(Box.createHorizontalGlue());
		addEventsButtonPanel.add(addButton);
		addEventsButtonPanel.add(Box.createHorizontalGlue());
		
		scrollPanel.add(Box.createVerticalStrut(10));
		scrollPanel.add(addEventsButtonPanel);

		addNewEventPanel();
	}
	
	/**
	 * Add new event panel to the wizard.
	 */
	private void addNewEventPanel() {
		SelectActivities_UI_EventPanel evPanel = new SelectActivities_UI_EventPanel(eventPanels.size(), eventClasses);
		evPanel.installPanelRemoveHandler(new EventPanelRemoveHandler(evPanel));
		
		eventPanels.add(evPanel);
		eventPanelContainer.add(evPanel.getPanel());
		
		// allow removal of event panels only of there is more than one
		for (SelectActivities_UI_EventPanel evPanel2 : eventPanels) {
			evPanel2.setCanBeRemoved(eventPanels.size() > 1);
		}
		
		scrollPanel.revalidate();
	}
	
	/**
	 * Handler that will remove event panels from the {@link SelectActivities_UI}.
	 * @author dfahland
	 */
	private class EventPanelRemoveHandler implements ActionListener {
		private SelectActivities_UI_EventPanel panel;
		public EventPanelRemoveHandler(SelectActivities_UI_EventPanel panel) {
			this.panel = panel;
		}
		public void actionPerformed(ActionEvent e) {
			eventPanels.remove(panel);
			eventPanelContainer.remove(panel.getPanel());
			
			int num = 0;
			for (SelectActivities_UI_EventPanel evPanel : eventPanels) {
				evPanel.setActivityNumber(num++);
				evPanel.refresh();
			}
			
			// if there is only one panel left, it cannot be removed
			if (eventPanels.size() == 1) {
				for (SelectActivities_UI_EventPanel evPanel : eventPanels) {
					evPanel.setCanBeRemoved(false);
				}
			}
			
			scrollPanel.revalidate();
		}
	}
	
	private XLog log;
	private List<XEventClass> eventClasses;
	private XEventClasses allEventClasses;
	
	private void updateEventClassSelection() {
		XEventClassifier chosen = (XEventClassifier)eventClassifierSelection.getSelectedItem();
		setEventClasses(chosen);
		
		for (SelectActivities_UI_EventPanel evPanel : eventPanels) {
			evPanel.setValues(eventClasses);
		}
	}
	
	private void setEventClasses(XEventClassifier classifier) {
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, classifier);
		allEventClasses = summary.getEventClasses();

		// sort event class
		List<XEventClass> classes = new ArrayList<XEventClass>(allEventClasses.getClasses());
		Collections.sort(classes);
		
		this.eventClasses = classes;
	}
	
	private List<XEventClassifier> getEventClassifiers(XLog log) {
		List<XEventClassifier> eventClassifiers = new LinkedList<XEventClassifier>();
		for (XEventClassifier classifier : log.getClassifiers()) {
			eventClassifiers.add(classifier);
		}
		// add default classifiers
		if (!eventClassifiers.contains(XLogInfoImpl.STANDARD_CLASSIFIER)) {
			eventClassifiers.add(XLogInfoImpl.STANDARD_CLASSIFIER);
		}
		if (!eventClassifiers.contains(XLogInfoImpl.NAME_CLASSIFIER)) {
			eventClassifiers.add(XLogInfoImpl.NAME_CLASSIFIER);
		}
		if (!eventClassifiers.contains(XLogInfoImpl.RESOURCE_CLASSIFIER)) {
			eventClassifiers.add(XLogInfoImpl.RESOURCE_CLASSIFIER);
		}
		return eventClassifiers;
	}
	
	/**
	 * @return all event classes in the log from which events were mapped to activities in the UI 
	 */
	public List<XEventClass> getEventClasses() {
		return new LinkedList<XEventClass>(eventClasses);
	}
	
	/**
	 * @return event classes with classifier in one object
	 */
	public XEventClasses getAllEventClasses() {
		return allEventClasses;
	}
	
	/**
	 * @return event classifier used to map events to activities
	 */
	public XEventClassifier getEventClassifier() {
		return (XEventClassifier)eventClassifierSelection.getSelectedItem();
	}
	
	protected void getParametersFromUI(Map<String,XEventClass> output) throws Exception {
		output.clear();
		
		for (SelectActivities_UI_EventPanel evPanel : eventPanels) {
			Pair<String,XEventClass> pair = evPanel.getConfigured(); 
			output.put(pair.getFirst(),pair.getSecond());
		}
	}

	protected String getTitle() {
		return DIALOG_TITLE;
	}
	
}