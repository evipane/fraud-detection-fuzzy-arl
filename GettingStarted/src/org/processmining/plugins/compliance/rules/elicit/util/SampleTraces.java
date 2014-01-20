package org.processmining.plugins.compliance.rules.elicit.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.compliance.AbstractLogForCompliance_Plugin;
import org.processmining.plugins.compliance.rules.elicit.ui.ProcessInstanceConformanceView;
import org.processmining.plugins.petrinet.replayresult.StepTypes;
import org.processmining.plugins.replayer.replayresult.SyncReplayResult;

import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class SampleTraces {
	/**
	 * Expand traces where subsequences of activities are parameterized to occur multiple times. If
	 * parameterMap is {@code null} or the parameter is not defined, then 1 instance will be generated
	 * 
	 * @param trace
	 * @param parameterMap
	 * @return the expanded trace
	 */
	public static List<String> expandTraceByParameter(List<String> trace, Map<String, Integer> parameterMap) {
		
		if (parameterMap == null) parameterMap = new HashMap<String, Integer>();
		
		// copy parameters to double for computation in expressions
		Map<String, Double> dMap = new HashMap<String, Double>();
		for (String p : parameterMap.keySet()) dMap.put(p, new Double(parameterMap.get(p)));
		
		List<String> iTrace = new LinkedList<String>();
		for (int i=0; i<trace.size(); i++) {
			String event = trace.get(i);
			
			// pattern for parameterized subtraces is
			// .... (k+1)*(A ... B) ...
			if (event.contains("*")) {
				int startIndex = i;
				int endIndex = i;
				for (; endIndex<trace.size(); endIndex++) {
					String endEvent = trace.get(endIndex);
					
					int par_start = endEvent.lastIndexOf('(')+1;
					int par_end = endEvent.indexOf(')', par_start);
					
					// endEvent has a closing parenthesis after the last opening parenthesis
					// (this will skip ignore closing brackets of the first parameter "(k+1)"
					if (endEvent.contains(")") && par_end >= 0) {
						break;
					}
				}
				
				// extract sequence that gets repeated
				List<String> subSequence = new LinkedList<String>();
				for (int j=startIndex; j<=endIndex; j++) {
					// clean annotations from events in the subsequence
					String sub_event = trace.get(j);
					int par_start = sub_event.lastIndexOf('(')+1;
					int par_end = sub_event.indexOf(')', par_start);
					if (par_end < 0) par_end = sub_event.length();
					
					System.out.println("instantiate "+sub_event+" to "+sub_event.substring(par_start, par_end)+" "+par_start+" "+par_end);
					
					sub_event = sub_event.substring(par_start, par_end);
					// add cleaned event to subsequence
					subSequence.add(sub_event);
				}
				
				String parameter = event.substring(0, event.indexOf('*'));
				Integer repeats = 1;
				try {
					Calculable expression = new ExpressionBuilder(parameter).withVariables(dMap).build();
					repeats = (int) expression.calculate();
				} catch (UnknownFunctionException e) {
				} catch (UnparsableExpressionException e) {
				}

				// add #repeat copies of the subSequence to the instantiated trace
				for (int r=0; r<repeats; r++) {
					for (String sub_event : subSequence) {
						iTrace.add(sub_event);
					}
				}
				
				i = endIndex;
			} else {
				// simple event without parameter
				iTrace.add(event);
			}
		}
		return iTrace;
	}
	
	public static List<String> instantiateTrace(List<String> trace, Map<String, XEventClass> activityMap, Map<String, Integer> parameterMap) {
		List<String> eTrace = SampleTraces.expandTraceByParameter(trace, parameterMap);
		
		List<String> iTrace = new LinkedList<String>();
		for (String event : eTrace) {
			if (activityMap.containsKey(event)) {
				iTrace.add(activityMap.get(event).toString());
			} else {
				iTrace.add(event);
			}
		}
		return iTrace;
	}
	
	/**
	 * Create a SyncReplayResult from a list of event names.
	 * 
	 * @param trace
	 * @param accepting
	 *            sets the movetypes shown for this trace, {@code true}
	 *            translates to {@link StepTypes#LMGOOD} and {@code false}
	 *            translates to {@link StepTypes#L}
	 * @return
	 */
	public static SyncReplayResult getFromTrace(List<String> trace) {
		
		List<StepTypes> stepTypes = new LinkedList<StepTypes>();
		List<Object> moves = new LinkedList<Object>();
		for (String e : trace) {
			moves.add(e);
			stepTypes.add(StepTypes.LMGOOD);
		}
		SyncReplayResult res = new SyncReplayResult(moves, stepTypes, 0);
		return res;
	}
	

	/**
	 * @param res
	 * @param name
	 * @param activityMap
	 * @return a JComponent visualizing example traces 
	 */
	public static JComponent getTraceVisualization(SyncReplayResult res, String name, Map<String,XEventClass> activityMap) {
		
		// reformat node instance list
		List<Object> result = new LinkedList<Object>();
		
		for (int i=0; i<res.getNodeInstance().size(); i++) {
			
			Object obj = res.getNodeInstance().get(i);
			
			if (obj instanceof Transition) {
				String label = ((Transition) obj).getLabel();
				if (activityMap.containsKey(label)) {
					String eventName = activityMap.get(label).toString();
					result.add(eventName);
				} else if (label.equalsIgnoreCase(AbstractLogForCompliance_Plugin.OMEGA_TRANSITION)){
					result.add(AbstractLogForCompliance_Plugin.OMEGA_CLASS_NAME);
				}

			} else if (obj instanceof String) {
				if (((String)obj).equalsIgnoreCase(AbstractLogForCompliance_Plugin.OMEGA_TRANSITION)){
					result.add(AbstractLogForCompliance_Plugin.OMEGA_CLASS_NAME);
				} else {
					result.add(obj);
				}
			} else {
				result.add(obj.toString());
			}
			
			Object inserted = result.get(i);

			if (inserted.equals(AbstractLogForCompliance_Plugin.OMEGA_TRANSITION) 
					|| inserted.equals(AbstractLogForCompliance_Plugin.OMEGA_CLASS_NAME) 
					|| inserted == AbstractLogForCompliance_Plugin.OMEGA_CLASS 
					|| inserted == AbstractLogForCompliance_Plugin.OMEGA_CLASS_NO_LIFECYCLE
					|| obj.equals(AbstractLogForCompliance_Plugin.OMEGA_TRANSITION) 
					|| obj.equals(AbstractLogForCompliance_Plugin.OMEGA_CLASS_NAME)
					|| obj == AbstractLogForCompliance_Plugin.OMEGA_CLASS 
					|| obj == AbstractLogForCompliance_Plugin.OMEGA_CLASS_NO_LIFECYCLE)
			{
				if (res.getStepTypes().get(i) != StepTypes.L && res.getStepTypes().get(i) != StepTypes.MREAL) {
					res.getStepTypes().set(i, StepTypes.MINVI);
				}
			}
		}
		
		
		// ALIGNMENT PANEL
		ProcessInstanceConformanceView alignmentPanel = new ProcessInstanceConformanceView(
				name, result, res.getStepTypes(), activityMap);
		
		// set scroll pane 
//		JScrollPane hscrollPane = new JScrollPane(alignmentPanel);
//		hscrollPane.setOpaque(true);
//		hscrollPane.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
//		hscrollPane.getViewport().setOpaque(true);
//		hscrollPane.getViewport().setBackground(WidgetColors.PROPERTIES_BACKGROUND);
//		hscrollPane.setBorder(BorderFactory.createEmptyBorder());
//		hscrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
//		hscrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//		JScrollBar hBar = hscrollPane.getHorizontalScrollBar();
//		hBar.setUI(new SlickerScrollBarUI(hBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
//				WidgetColors.COLOR_NON_FOCUS, 4, 12));
//		hBar.setOpaque(true);
//		hBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
//		hBar = hscrollPane.getHorizontalScrollBar();
//		hBar.setUI(new SlickerScrollBarUI(hBar, new Color(0, 0, 0, 0), new Color(160, 160, 160),
//				WidgetColors.COLOR_NON_FOCUS, 4, 12));
//		hBar.setOpaque(true);
//		hBar.setBackground(WidgetColors.PROPERTIES_BACKGROUND);
		
		return alignmentPanel;
	}
	
}
