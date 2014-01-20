package org.processmining.plugins.compliance.temporal;

import java.awt.Color;
import java.awt.Dimension;
import java.util.Arrays;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClasses;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.util.ArrayUtils;
import org.processmining.framework.util.ui.widgets.ProMScrollablePanel;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.plugins.compliance.temporal.TemporalComplianceRequirement.EventTimeStampInit;

import com.fluxicon.slickerbox.factory.SlickerFactory;
import com.fluxicon.slickerbox.ui.SlickerScrollBarUI;

public class EnrichLog_UI  extends JPanel {

	private static final long serialVersionUID = 1L;
	
	public static final String DIALOG_TITLE = "Enrich Log for Temporal Compliance Checking";
	
	private LinkedList<TimeStampInitialization_UI> c_timeStampInits = new LinkedList<TimeStampInitialization_UI>();
	
	private XEventClasses eventClasses = null;
	
	private static class TimeStampInitialization_UI {
		public JLabel 	 		name;
		public JComboBox 		selectedActivity;
		
		public String			nameInPattern;
		
		public JPanel			container;
		
		public TimeStampInitialization_UI(String nameInPattern, Object[] activityOptions, Object preSelected) {
			initialize(nameInPattern, activityOptions, preSelected);
		}
		
		private void initialize(String nameInPattern, Object[] eClassOptions, Object preSelected) {
			
			this.nameInPattern = nameInPattern;

			SlickerFactory f = SlickerFactory.instance();
			name = f.createLabel(nameInPattern);
			
			selectedActivity = f.createComboBox(eClassOptions);
			selectedActivity.setMinimumSize(new Dimension(200, 30));
			selectedActivity.setPreferredSize(new Dimension(300, 30));
			selectedActivity.setMaximumSize(new Dimension(400, 30));
			selectedActivity.setSelectedItem(preSelected);
			
			container = new JPanel();
			container.setOpaque(false);
			container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
			
			JPanel 	line = f.createRoundedPanel(10, WidgetColors.PROPERTIES_BACKGROUND);
			int line_height = 34;
			line.setMinimumSize(new Dimension(300, line_height));
			line.setPreferredSize(new Dimension(500, line_height));
			line.setMaximumSize(new Dimension(1000, line_height));
			line.setLayout(new BoxLayout(line, BoxLayout.X_AXIS));
			
			line.add(name);
			line.add(Box.createHorizontalGlue());
			
			JLabel initLabel = f.createLabel("initialize by");
			line.add(initLabel);
			line.add(Box.createHorizontalStrut(5));
			line.add(selectedActivity);

			container.add(line);
			container.add(Box.createVerticalStrut(5));
		}
	}
	
	
	public EnrichLog_UI(TemporalComplianceRequirement req) {
		
		this.eventClasses = req.allEventClasses;
		
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
		
		Object[] activityNames = req.patterNameToEventID.keySet().toArray();
		activityNames = createActivitySelection(activityNames);
		
		for (String usedEvent : req.getGuardEvents()) {
			TimeStampInitialization_UI ts_init = new TimeStampInitialization_UI(usedEvent, activityNames, CreateTemporalPattern_Plugin.T_INSTANCE_START);
			c_timeStampInits.add(ts_init);
			scrollPanel.add(ts_init.container);
		}
	}
	
	private XEventClass getEventClassForID(String eventClassId) {
		for (XEventClass cl : eventClasses.getClasses()) {
			if (cl.getId().equals(eventClassId)) {
				return cl;
			}
		}
		return null;
	}
	
	private Object[] createActivitySelection(Object[] activityNames) {

		Arrays.sort(activityNames);
		Object[] notMappedAct = { "NONE" };
		Object[] boxOptions = ArrayUtils.concatAll(notMappedAct, activityNames);
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
	public InteractionResult setParameters(UIPluginContext context, TemporalComplianceRequirement req) {
		InteractionResult wish = getUserChoice(context);
		if (wish != InteractionResult.CANCEL) setParametersFromUI(req);
		return wish;
	}
	
	protected void setParametersFromUI(TemporalComplianceRequirement req) {
		
		for (TimeStampInitialization_UI ts_init_ui : c_timeStampInits) {
			String activity_initFor = ts_init_ui.nameInPattern;
			String eventClassId_initFor = req.patterNameToEventID.get(activity_initFor);
			XEventClass initFor = getEventClassForID(eventClassId_initFor);
			
			if (initFor == null) {
				JOptionPane.showMessageDialog(this, "Error. Could not find event class for '"+activity_initFor+"' ('"+eventClassId_initFor+"')");
			}

			if (ts_init_ui.selectedActivity.getSelectedIndex() > 0) {
				String activity_initBy = (String)ts_init_ui.selectedActivity.getSelectedItem();
				String eventClassId_initBy = req.patterNameToEventID.get(activity_initBy); 
				XEventClass initBy = getEventClassForID(eventClassId_initBy);
				
				if (initBy == null) {
					JOptionPane.showMessageDialog(this, "Error. Could not find event class for '"+activity_initBy+"' ('"+eventClassId_initBy+"')");
				}
				
				EventTimeStampInit ts_init = new EventTimeStampInit();
				ts_init.initFor = initFor;
				ts_init.initBy = initBy;
				ts_init.activity_initFor = activity_initFor;
				ts_init.activity_initBy = activity_initBy;
				req.eventTimeStampInitSpec.add(ts_init);
			}
		}
	}
}
