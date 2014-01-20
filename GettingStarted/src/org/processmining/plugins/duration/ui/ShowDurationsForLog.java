package org.processmining.plugins.duration.ui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

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

/**
 * 
 * 
 * @author Wiebe E. Nauta (wiebenauta@gmail.com)
 */
@Plugin(name = "Show durations for log", returnLabels = { "Durations" }, returnTypes = { JComponent.class }, parameterLabels = "Map of durations")
@Visualizer
public class ShowDurationsForLog {

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Wiebe E. Nauta", email = "wiebenauta@gmail.com")
	@PluginVariant(requiredParameterLabels = { 0 })
	public static JComponent showDurations(PluginContext context, Map<String, Map<DurationID, Duration>> traceDurationMap) {

		JTextArea textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		textArea.setFont(new Font("Courier", Font.PLAIN, 12));
		textArea.setText("");
		
		Set<Entry<String, Map<DurationID, Duration>>> set = traceDurationMap.entrySet();
		List<Entry<String, Map<DurationID, Duration>>> entries = new ArrayList<Entry<String, Map<DurationID, Duration>>>(set);
		Collections.sort(entries, new LogDurationsComparator());
		for (Entry<String, Map<DurationID, Duration>> entry : entries) {
			String traceID = entry.getKey();
			Map<DurationID, Duration> durationMap = entry.getValue();
			List<Duration> durations = new ArrayList<Duration>(durationMap.values());
			Collections.sort(durations, new DurationsComparator());
			for (Duration duration : durations) {
				textArea.append("Trace\t" + traceID + "\t" + duration + "\n");
			}
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
	
	public static class LogDurationsComparator implements Comparator<Entry<String, ?>> {
		public int compare(Entry<String, ?> o1, Entry<String, ?> o2) {
			if (o1.getKey() == null)
				if (o2.getKey() == null)
					return 0;
				else
					return o2.getKey().compareTo(o1.getKey()) * -1;
			else
				return o1.getKey().compareTo(o2.getKey());
		}
	}
	
	public static class DurationsComparator implements Comparator<Duration> {
		public int compare(Duration o1, Duration o2) {
			if (o1.startTime == null)
				if (o2.startTime == null)
					return 0;
				else
					return o2.startTime.compareTo(o1.startTime) * -1;
			else
				return o1.startTime.compareTo(o2.startTime);
		}
	}
}
