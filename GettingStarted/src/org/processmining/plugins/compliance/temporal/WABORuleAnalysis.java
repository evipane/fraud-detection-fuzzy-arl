package org.processmining.plugins.compliance.temporal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.plugins.DataConformance.Alignment;
import org.processmining.plugins.DataConformance.ResultReplay;
import org.processmining.plugins.DataConformance.visualization.DataAwareStepTypes;

public class WABORuleAnalysis {

	private final String nextColumn = "\t";
	private final String nextLine = "\r\n";

	private XConceptExtension cExt = XConceptExtension.instance();
	private XTimeExtension tExt = XTimeExtension.instance();
	private XOrganizationalExtension oExt = XOrganizationalExtension.instance();

	private SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

	@Plugin(
			name = "00Compliance WABO3 post processor to Excel",
				parameterLabels = { "Event log", "Data Replay Result" },
				returnLabels = { "Event Log with artificial start/end events" },
				returnTypes = { String.class },
				userAccessible = true,
				help = "!!!!!Especially for Elham for WABO analysis!!!!")
	@UITopiaVariant(
			uiLabel = "00Compliance WABO3 post processor to Excel",
				affiliation = "Eindhoven University of Technology",
				author = "J.C.A.M.Buijs",
				email = "j.c.a.m.buijs@tue.nl",
				pack = "JoosBuijs")
	public String processDataReplayForElham(final PluginContext context, XLog eventlog, ResultReplay replayResult) {
		/*
		 * DISCLAIMER: the following code is not to be used as a 'good example'
		 * but more as a 'it does the job' state of the art.
		 */
		
		String startActivity = "01_HOOFD_010";
		String endActivityVariable = "var_01_HOOFD_490_3_complete";
		String startPauzeActivity = "08_AWB45_020_2";
		String startPauzeActivityVariable = "var_08_AWB45_020_2_complete";
		String endPauzeActivity = "08_AWB45_040";
		String endPauzeActivityVariable = "var_08_AWB45_040_complete";
		

		Set<String> violatingTraces = new HashSet<String>();
		HashMap<String, List<Date>> traceInfo = new HashMap<String, List<Date>>();
		//First, collect the trace names from the replay result
		for (Alignment alignment : replayResult.labelStepArray) {
			String traceName = alignment.getTraceName();
			String traceString = traceName.substring(traceName.indexOf(" "));
			traceString = traceString.trim();

			Date expectedDate = null;
			Date realDate = null;
			for (int i = 0; i < alignment.getStepTypes().size(); i++) {
				DataAwareStepTypes stepType = alignment.getStepTypes().get(i);
				//If the step type is LMNOGOOD, we get the info from the first one
				if (stepType.equals(DataAwareStepTypes.LMNOGOOD)) {
					//Get the expected date from the process steps
					
					expectedDate = (Date) alignment.getProcessTrace().get(i).get(endActivityVariable);

					//And the real one from the log
					realDate = (Date) alignment.getLogTrace().get(i).get(endActivityVariable);
				}
			}

			ArrayList<Date> dateList = new ArrayList<Date>();
			dateList.add(expectedDate);
			dateList.add(realDate);
			traceInfo.put(traceString, dateList);
			
			if (alignment.getFitness() < 1) {
				violatingTraces.add(traceString);
			}
		}

		StringBuilder outputString = new StringBuilder();

		//Build the column names
		outputString.append("Trace ID" + nextColumn + "start date" + nextColumn + "Pauze start" + nextColumn
				+ "Pauze end" + nextColumn + "Expected End" + nextColumn + "Real End" + nextColumn + "Last Phase"
				+ nextColumn + "Resources" + nextColumn + "Violation");
		outputString.append(nextLine);

		//Then, loop through the log, looking for those traces, building up the string
		for (XTrace t : eventlog) {
			String tName = cExt.extractName(t);
			if (traceInfo.keySet().contains(tName)) {
				//TRACE NAME
				outputString.append(tName + nextColumn);

				/*
				 * Collect the base data
				 */
				Date caseStart = null;
				Date pauzeStart = null;
				Date pauzeEnd = null;
				Date expectedEnd = traceInfo.get(tName).get(0);
				Date caseEnd = traceInfo.get(tName).get(1);
				HashSet<String> resources = new HashSet<String>();

				for (XEvent event : t) {
					String eventName = cExt.extractName(event);

					if (eventName.equals(startActivity)) {
						caseStart = tExt.extractTimestamp(event);
					} else if (eventName.equals(startPauzeActivity)) {
						pauzeStart = tExt.extractTimestamp(event);
					} else if (eventName.equals(endPauzeActivity)) {
						pauzeEnd = tExt.extractTimestamp(event);
					}

					resources.add(oExt.extractResource(event));
				}

				/*
				 * Calculate and write the data
				 */
				//The case start timestamp
				String caseStartString = "";
				if (caseStart != null) {
					caseStartString = df.format(caseStart);
				}
				outputString.append(caseStartString + nextColumn);

				//Pauze start timestamp, which might be NULL
				String pauzeStartString = "";
				if (pauzeStart != null) {
					pauzeStartString = df.format(pauzeStart);
				}
				outputString.append(pauzeStartString + nextColumn);

				//Pauze end timestamp
				String pauzeEndString = "";
				if (pauzeEnd != null) {
					pauzeEndString = df.format(pauzeEnd);
				}
				outputString.append(pauzeEndString + nextColumn);

				//Expected End timestamp
				String expectedEndString = "";
				if (expectedEnd != null) {
					expectedEndString = df.format(expectedEnd);
				}
				outputString.append(expectedEndString + nextColumn);

				//Real end timestamp
				String caseEndString = "";
				if (caseEnd != null) {
					caseEndString = df.format(caseEnd);
				}
				outputString.append(caseEndString + nextColumn);

				//Last known phase of the trace
				String lastPhase = "";
				for (Entry<String, XAttribute> entry : t.getAttributes().entrySet()) {
					if (entry.getKey().equals("last_phase")) {
						lastPhase = entry.getValue().toString();
					}
				}
				outputString.append(lastPhase);
				outputString.append(nextColumn);

				//All resources that participated in this case
				resources.remove("Artificial Start");
				resources.remove("Artificial End");
				for (String r : resources) {
					if (!(r == null || r.equals(""))) {
						outputString.append(r + ", ");
					}
				}
				outputString.append(nextColumn);
				
				if (violatingTraces.contains(tName)) outputString.append("violation");
				else outputString.append("compliant");

				outputString.append(nextLine);
			}
		}

		String o = outputString.toString();

		System.out.println(o);

		return o;
	}

}
