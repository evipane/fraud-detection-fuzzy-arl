package org.processmining.plugins.compliance.rules.select.ui;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.util.Pair;
import org.processmining.framework.util.ui.widgets.ProMComboBox;
import org.processmining.framework.util.ui.widgets.WidgetColors;
import org.processmining.plugins.compliance.rules.ui.widgets.Structured_UI;
import org.processmining.plugins.compliance.rules.ui.widgets.UIUtils;

import com.fluxicon.slickerbox.factory.SlickerFactory;

public class SelectActivities_UI_EventPanel implements Structured_UI<List<XEventClass>, Pair<String, XEventClass>>{
	private JPanel container;
	private ProMComboBox eventBox;
	
	private JLabel activityName;
	private JButton removeButton;
	private JPanel removeButtonReplacement;
	
	private boolean canBeRemoved = true;
	
	private int activityNumber = 0;
	
	public SelectActivities_UI_EventPanel(int num, List<XEventClass> eventClasses) {
		SlickerFactory f = SlickerFactory.instance();
		
		setActivityNumber(num);
		
		container = new JPanel();
		container.setOpaque(false);
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		
		JPanel panel = f.createRoundedPanel(10, WidgetColors.PROPERTIES_BACKGROUND);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		activityName = f.createLabel(getActivityName());
		
		eventBox = new ProMComboBox(eventClasses);
		
		UIUtils.setFixedSize(eventBox, new Dimension(250, 30));
		removeButton = UIUtils.createCrossSignButton();
		removeButton.setVisible(false);
		removeButtonReplacement = new JPanel();
		removeButtonReplacement.setOpaque(false);
		removeButtonReplacement.setMinimumSize(new Dimension(30, 30));
		removeButtonReplacement.setPreferredSize(new Dimension(30, 30));
		removeButtonReplacement.setMaximumSize(new Dimension(30, 30));
		removeButtonReplacement.setVisible(true);
		
		
		panel.add(activityName);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(eventBox);
		panel.add(Box.createHorizontalGlue());
		panel.add(removeButton);
		panel.add(removeButtonReplacement);
		
		container.add(panel);
		container.add(Box.createVerticalStrut(2));
		
		installHighlighter(panel);
	}
	
	public void setValues(List<XEventClass> input) {
		eventBox.removeAllItems();
		for (XEventClass cl : input) {
			eventBox.addItem(cl);
		}
		
		refresh();
	}
	
	public void refresh() {
		activityName.setText(getActivityName());
		container.revalidate();
	}
	
	public Pair<String, XEventClass> getConfigured() throws Exception {
		return new Pair<String, XEventClass>(activityName.getText(), (XEventClass)eventBox.getSelectedItem());
	}

	public Component getPanel() {
		return container;
	}
	
	protected void installPanelRemoveHandler(ActionListener listener) {
		removeButton.addActionListener(listener);
	}
	
	protected void setCanBeRemoved(boolean canBeRemoved) {
		this.canBeRemoved = canBeRemoved;
		if (!canBeRemoved) {
			removeButton.setVisible(false);
			removeButtonReplacement.setVisible(true);
		}
	}
	
	/**
	 * called when the mouse enters the panel
	 */
	protected void handlePanelIsActive() {
		if (canBeRemoved) {
			removeButton.setVisible(true);
			removeButtonReplacement.setVisible(false);
		}
	}
	
	/**
	 * called when the mouse leaves the panel
	 */
	protected void handlePanelIsInActive() {
		if (canBeRemoved) {
			removeButton.setVisible(false);
			removeButtonReplacement.setVisible(true);
		}
	}
	
	private String getActivityName() {
		int num = getActivityNumber();
		char c = (char)('A' + num);
		return Character.toString(c);
	}
	
	public int getActivityNumber() {
		return activityNumber;
	}

	public void setActivityNumber(int activityNumber) {
		this.activityNumber = activityNumber;
	}

	private void installHighlighter(final Component component) {
		component.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(final MouseEvent arg0) { /* ignore */
			}

			@Override
			public void mouseEntered(final MouseEvent arg0) {
				handlePanelIsActive();
			}

			@Override
			public void mouseExited(final MouseEvent arg0) {
				handlePanelIsInActive();
			}

			@Override
			public void mousePressed(final MouseEvent arg0) { /* ignore */
			}

			@Override
			public void mouseReleased(final MouseEvent arg0) { /* ignore */
			}
		});
		if (component instanceof Container) {
			for (final Component child : ((Container) component).getComponents()) {
				installHighlighter(child);
			}
		}
	}
}
