package org.processmining.plugins.compliance.temporal;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.info.impl.XLogInfoImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ArrayUtils;
import org.processmining.framework.util.ui.widgets.ProMScrollablePanel;
import org.processmining.framework.util.ui.widgets.ProMTextField;
import org.processmining.framework.util.ui.widgets.WidgetColors;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

public class CreateTemporalPattern_UI  extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public static final String DIALOG_TITLE = "Temporal Compliance Checking";
	
	private ArrayList<EventConfiguration_UI> pickedEvents = new ArrayList<EventConfiguration_UI>();
	private JPanel pickedEventsPanel;
	
	private ArrayList<EventConfiguration_UI> predefinedEvents = new ArrayList<EventConfiguration_UI>();
	private JPanel predefinedEventsPanel;

	private XEventClasses c_eventClasses = null;
	private Object[] eventClasses;
	
	private final XLog log;
	
	private static class EventConfiguration_UI {
		public JLabel 	 		name;
		public JComboBox 		event;
		public ProMTextField 	guard;
		
		public JPanel			container;
		
		public EventConfiguration_UI(int num, Object[] eventClasses) {
			String eventName = Character.toString((char) ('A'+num));
			initialize(eventName, eventClasses);
		}
		
		public EventConfiguration_UI(String eventName, Object[] eventClasses) {
			initialize(eventName, eventClasses);
		}
		
		private void initialize(String eventName, Object[] eventClasses) {
			SlickerFactory f = SlickerFactory.instance();
			name = f.createLabel(eventName);
			
			if (eventClasses != null) {
				event = f.createComboBox(eventClasses);
				event.setMinimumSize(new Dimension(200, 30));
				event.setPreferredSize(new Dimension(300, 30));
				event.setMaximumSize(new Dimension(400, 30));
			}
			
			guard = new ProMTextField();
			guard.setMinimumSize(new Dimension(200, 30));
			guard.setPreferredSize(new Dimension(300, 30));
			guard.setMaximumSize(new Dimension(400, 30));
			
			container = new JPanel();
			container.setOpaque(false);
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
			
			JPanel line;
			int    line_height;
			if (event != null) {
				line = f.createRoundedPanel(10, WidgetColors.PROPERTIES_BACKGROUND);
				line_height = 34;
			} else {
				line = new JPanel();
				line.setOpaque(false);
				line_height = 30;
			}
			line.setMinimumSize(new Dimension(300, line_height));
			line.setPreferredSize(new Dimension(500, line_height));
			line.setMaximumSize(new Dimension(1000, line_height));
			line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
			
			line.add(name);
			if (event != null) {
				line.add(Box.createHorizontalStrut(5));
				line.add(event);
			}
			
			line.add(Box.createHorizontalGlue());
			
			JLabel guardLabel = f.createLabel("guard");
			line.add(guardLabel);
			line.add(Box.createHorizontalStrut(5));
			line.add(guard);

			container.add(line);
			container.add(Box.createVerticalStrut(5));
		}
	}
	
	public CreateTemporalPattern_UI(XLog log, TemporalComplianceRequirement config) {
		
		this.log = log;
		
		final SlickerFactory f = SlickerFactory.instance();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		// label
		add(f.createLabel("<html><h1>Select Temporal Pattern</h1>" +
				"<p>Pick a pre-defined pattern or define one on your own.</p></html>"));
		
		JPanel scrollPanel = new ProMScrollablePanel();
		scrollPanel.setOpaque(false);
		scrollPanel.setLayout(new BoxLayout(scrollPanel, BoxLayout.Y_AXIS));
		
		final JScrollPane scrollPane = new JScrollPane(scrollPanel);
		scrollPane.setOpaque(false);
		scrollPane.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		scrollPane.getViewport().setOpaque(false);
		scrollPane.getViewport().setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		JScrollBar vBar = scrollPane.getVerticalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(false);
		vBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		vBar = scrollPane.getHorizontalScrollBar();
		vBar.setUI(new SlickerScrollBarUI(vBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
				WidgetColors.COLOR_NON_FOCUS, 4, 12));
		vBar.setOpaque(false);
		vBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		add(scrollPane);
		
		pickedEventsPanel = new JPanel();
		pickedEventsPanel.setOpaque(false);
		pickedEventsPanel.setLayout(new BoxLayout(pickedEventsPanel, BoxLayout.Y_AXIS));
		
		eventClasses = createEventClassSelection(CreateTemporalPattern_UI.this.log, XLogInfoImpl.STANDARD_CLASSIFIER);
		
		final JButton addEventButton = f.createButton("+");
		addEventButton.setOpaque(false);
		addEventButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventConfiguration_UI event = new EventConfiguration_UI(pickedEvents.size(), eventClasses);
				pickedEvents.add(event);
				pickedEventsPanel.add(event.container);
				CreateTemporalPattern_UI.this.validate();
			}
		});
		
		final JButton removeEventButton = f.createButton("-");
		removeEventButton.setOpaque(false);
		removeEventButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (pickedEvents.size() > 0) {
					EventConfiguration_UI cb = pickedEvents.remove(pickedEvents.size()-1);
					pickedEventsPanel.remove(cb.container);
					CreateTemporalPattern_UI.this.validate();
				}
			}
		});
		
		JPanel add_remove_Panel = new JPanel();
		add_remove_Panel.setOpaque(false);
		add_remove_Panel.setLayout(new BoxLayout(add_remove_Panel, BoxLayout.X_AXIS));
		add_remove_Panel.add(removeEventButton);
		add_remove_Panel.add(addEventButton);
		
		addDefaultTransitions();
		
		scrollPanel.add(pickedEventsPanel);
		scrollPanel.add(Box.createVerticalStrut(10));
		scrollPanel.add(add_remove_Panel);
		scrollPanel.add(Box.createVerticalStrut(10));
		scrollPanel.add(predefinedEventsPanel);
		
	}
	
	private void addDefaultTransitions() {
		predefinedEvents.add(new EventConfiguration_UI(CreateTemporalPattern_Plugin.T_START, null));
		predefinedEvents.add(new EventConfiguration_UI(CreateTemporalPattern_Plugin.T_INSTANCE_START, null));
		predefinedEvents.add(new EventConfiguration_UI(CreateTemporalPattern_Plugin.T_INSTANCE_COMPLETE, null));
		predefinedEvents.add(new EventConfiguration_UI(CreateTemporalPattern_Plugin.T_END, null));
		
		predefinedEventsPanel = new JPanel();
		predefinedEventsPanel.setOpaque(false);
		predefinedEventsPanel.setLayout(new BoxLayout(predefinedEventsPanel, BoxLayout.Y_AXIS));
		for (EventConfiguration_UI e : predefinedEvents) {
			predefinedEventsPanel.add(e.container);
		}
		predefinedEventsPanel.validate();
	}
	
	/**
	 * get all available event classes using the selected classifier, add with
	 * NONE
	 * 
	 * @param log
	 * @param selectedItem
	 * @return
	 */
	private XEventClasses extractEventClasses(XLog log, XEventClassifier eventClassifier) {
		XLogInfo summary = XLogInfoFactory.createLogInfo(log, eventClassifier);
		c_eventClasses = summary.getEventClasses();
		return c_eventClasses;
	}
	
	/**
	 * get all available event classes using the selected classifier, add with
	 * NONE
	 * 
	 * @param log
	 * @param selectedItem
	 * @return
	 */
	private Object[] createEventClassSelection(XLog log, XEventClassifier eventClassifier) {
		// sort event class
		Collection<XEventClass> classes = extractEventClasses(log, eventClassifier).getClasses();

		// create possible event classes
		Object[] arrEvClass = classes.toArray();
		Arrays.sort(arrEvClass);
		//Object[] notMappedAct = { "NONE" };
		Object[] notMappedAct = { } ;
		Object[] boxOptions = ArrayUtils.concatAll(notMappedAct, arrEvClass);

		return boxOptions;
	}
	
	/**
	 * display a dialog to ask user what to do
	 * 
	 * @param context
	 * @return
	 */
	protected InteractionResult getUserChoice(UIPluginContext context) {
		return context.showConfiguration(DIALOG_TITLE, this);
	}
	
	/**
	 * Open UI dialogue to populate the given configuration object with
	 * settings chosen by the user.
	 * 
	 * @param context
	 * @param config
	 * @return result of the user interaction
	 */
	public InteractionResult setParameters(UIPluginContext context, TemporalComplianceRequirement config) {
		InteractionResult wish = getUserChoice(context);
		if (wish != InteractionResult.CANCEL) setParametersFromUI(config);
		return wish;
	}
	
	protected void setParametersFromUI(TemporalComplianceRequirement config) {
		config.patterNameToEventID.clear();
		config.guards.clear();
		for (EventConfiguration_UI event : pickedEvents) {
			XEventClass eClass = (XEventClass)event.event.getSelectedItem();
			config.patterNameToEventID.put(event.name.getText(), eClass.toString());
			if (event.guard.getText() != null && event.guard.getText().length() > 0) {
				config.guards.put(event.name.getText(), event.guard.getText());
			}
		}
		
		for (EventConfiguration_UI event : predefinedEvents) {
			config.patterNameToEventID.put(event.name.getText(), event.name.getText()+"+complete");
			if (event.guard.getText() != null && event.guard.getText().length() > 0) {
				config.guards.put(event.name.getText(), event.guard.getText());
			}
		}
		
		config.allEventClasses = c_eventClasses;
	}
}
