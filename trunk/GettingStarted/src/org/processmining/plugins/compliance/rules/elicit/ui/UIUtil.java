package org.processmining.plugins.compliance.rules.elicit.ui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.classification.XEventClass;

public class UIUtil {
	
	public static String getWrappedLabel(String original, int line_length) {

		StringBuffer wrappedLabel = new StringBuffer();
		int nextWord = 0;
		int currentWord = 0;
		int lastWrap = 0;
		
		boolean inHtml = false;
		
		while ((nextWord = original.indexOf(' ', currentWord+1)) > 0) {
			int current_line_length = 0;
			
			for (int i=lastWrap; i<currentWord; i++) {
				if (original.charAt(i) == '<') {
					inHtml = true;
				}
				if (original.charAt(i) == '>') {
					inHtml = false;
				}
				
				if (!inHtml) current_line_length++; 
			}
			
			if (current_line_length > line_length && lastWrap != currentWord && !inHtml) {
				String line = original.substring(lastWrap, currentWord);
				wrappedLabel.append(line);
				wrappedLabel.append("<br />");
				lastWrap = currentWord;
			}
			currentWord = nextWord;
		}
		wrappedLabel.append(original.substring(lastWrap));
		
		return wrappedLabel.toString();
	}
	
	public static final String EVENT_COLORS_HTML[] = { 
		"#9C072E",
		"#1972BA",
		"#BE7D1E",
		"#609A40",
		"#A954C7",
		"#837B57",
		"#A3567A",
		"#647E97" };
	
	private static Color rgb(int r, int g, int b) {
		return new Color(r,g,b);
	}
	
	public static final Color EVENT_COLORS_RGB[] = {
		rgb(156,7,46),
		rgb(25,114,186),
		rgb(190,125,30),
		rgb(96,154,64),
		rgb(169,84,199),
		rgb(131,123,87),
		rgb(163,86,122),
		rgb(100,126,151)
	};
	
	public static String getHTMLColorFor(String activityName) {
		int index = activityName.charAt(0)-'A';
		return EVENT_COLORS_HTML[index];
	}
	
	public static Color getRGBColorFor(String activityName) {
		int index = activityName.charAt(0)-'A';
		return EVENT_COLORS_RGB[index];
	}
	
	/** 
	 * Replace the key $A, $B, ... by the event name mapped to that activity in
	 * a pre-defined color (as defined by {@link #EVENT_COLORS_HTML}). Coloring is done
	 * via {@code HTML formatting}, so the event name has to be placed in an
	 * HTML formatted label.
	 * 
	 * @param s
	 * @param key
	 * @param name
	 * @return
	 */
	public static String insertActivity_colored(String s, String key, String name) {
		String dKey = "$"+key;
		String coloredName = "<font color='"+getHTMLColorFor(key)+"'>"+name+"</font>";
		s = s.replace(dKey, coloredName);

		return s;
	}
	
	public static String refreshText(String updatedAnswer, Map<String, XEventClass> activityMap, Map<String, Integer> parameterMap) {
		List<String> activities = new ArrayList<String>(activityMap.keySet());
		Collections.sort(activities);
		for (int i=activities.size()-1;i>=0;i--) {
			String key = activities.get(i);
			updatedAnswer = insertActivity_colored(updatedAnswer, key, activityMap.get(key).toString());
		}
		for (String key : parameterMap.keySet()) {
			String dKey = "$"+key;
			updatedAnswer = updatedAnswer.replace(dKey, parameterMap.get(key).toString());
		}
		
		StringBuffer wrappedLabel = new StringBuffer();
		wrappedLabel.append("<html><p>");
		wrappedLabel.append(getWrappedLabel(updatedAnswer, 80));
		wrappedLabel.append("</p></html>");
		
		return wrappedLabel.toString();
	}
}
