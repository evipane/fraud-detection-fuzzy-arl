package org.processmining.plugins.duration.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.duration.DurationIDs.DurationID;
import org.processmining.plugins.duration.Durations.Duration;
import org.processmining.plugins.duration.ui.ShowDurationsForLog.DurationsComparator;

/**
 * 
 * 
 * @author Wiebe E. Nauta (wiebenauta@gmail.com)
 */
@Plugin(name = "Show durations for trace", returnLabels = { "Durations" }, returnTypes = { JComponent.class }, parameterLabels = "List of durations")
@Visualizer
public class ShowDurationsForTrace {

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Wiebe E. Nauta", email = "wiebenauta@gmail.com")
	@PluginVariant(requiredParameterLabels = { 0 })
	public static JComponent showDurations(PluginContext context, Map<DurationID, Duration> durationMap) {

		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setFont(new Font("Courier", Font.PLAIN, 12));
		textArea.setText("");

		List<Duration> durations = new ArrayList<Duration>(durationMap.values());
		Collections.sort(durations, new DurationsComparator());
		for (Duration duration : durations) {
			textArea.append(duration + "\n");
		}

		JScrollPane scrollPane = new JScrollPane(textArea);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1.0;
		c.weighty = 1.0;
		panel.add(scrollPane, c);

		return panel;
	}
}