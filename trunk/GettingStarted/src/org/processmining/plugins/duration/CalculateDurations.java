package org.processmining.plugins.duration;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.plugins.duration.DurationIDs.ActivityDurationID;
import org.processmining.plugins.duration.DurationIDs.DurationID;
import org.processmining.plugins.duration.DurationIDs.ResourceDurationID;
import org.processmining.plugins.duration.DurationStates.DurationState;
import org.processmining.plugins.duration.DurationTypes.ActivityDurationType;
import org.processmining.plugins.duration.DurationTypes.ResourceDurationType;
import org.processmining.plugins.duration.Durations.ActivityDuration;
import org.processmining.plugins.duration.Durations.Duration;
import org.processmining.plugins.duration.Durations.ResourceDuration;

/**
 * This class offers 4 plug-in variants through which activity and resource
 * durations for a log or trace that conforms to
 * org.deckfour.xes.extension.std.XLifecycleExtension.StandardModel can be
 * calculated.
 * <p>
 * See the XES standard definition documentation for the lifecycle states and
 * transitions. The durations are shown in the following tables.
 * <table>
 * <tr>
 * <th>Activity duration</th>
 * <th>Start event(s)</th>
 * <th>Completion end event(s)</th>
 * <th>Cancellation end event(s)</th>
 * </tr>
 * <tr>
 * <td>Waiting</td>
 * <td>schedule</td>
 * <td>start, manualskip</td>
 * <td>withdraw, pi_abort</td>
 * </tr>
 * <tr>
 * <td>Working</td>
 * <td>start</td>
 * <td>complete</td>
 * <td>ate_abort, pi_abort</td>
 * </tr>
 * <tr>
 * <td>Suspended</td>
 * <td>suspend</td>
 * <td>resume</td>
 * <td>ate_abort, pi_abort</td>
 * </tr>
 * </table>
 * <p>
 * <table>
 * <tr>
 * <th>Resource duration</th>
 * <th>Start event(s)</th>
 * <th>Completion end event(s)</th>
 * <th>Cancellation end event(s)</th>
 * </tr>
 * <tr>
 * <td>Assigned</td>
 * <td>schedule, assign</td>
 * <td>reassign</td>
 * <td>withdraw, pi_abort</td>
 * </tr>
 * <tr>
 * <td>Allocated</td>
 * <td>schedule, assign, reassign</td>
 * <td>start, manualskip</td>
 * <td>withdraw, pi_abort</td>
 * </tr>
 * <tr>
 * <td>Busy</td>
 * <td>start</td>
 * <td>complete</td>
 * <td>ate_abort, pi_abort</td>
 * </tr>
 * </table>
 * <p>
 * Per trace the plug-ins assume the occurrence of the attribute:
 * <ul>
 * <li>concept:name</li>
 * </ul>
 * <p>
 * Per event in a trace the plug-ins assume occurrence of the attribute:
 * <ul>
 * <li>concept:name</li>
 * <li>time:timestamp</li>
 * <li>lifecycle:transition</li>
 * </ul>
 * <p>
 * Because there can be multiple instance executions of the same task or the
 * task can be executed multiple times in a loop, the calculation has to take
 * into account the instance of the event through the event attribute:
 * <ul>
 * <li>concept:instance</li>
 * </ul>
 * <p>
 * If this attribute is missing it is assumed there are no multiple instance
 * executions or loops in the process the log originates from.
 * <p>
 * Resource durations are calculated through the organizational event
 * attributes. Only one attributed of the following is considered, in given
 * order:
 * <ul>
 * <li>org:resource</li>
 * <li>org:role</li>
 * <li>org:group</li>
 * </ul>
 * <p>
 * Of course, transitions in the lifecycle may be skipped. E.g. a trace with
 * just a start and complete event will be processed normally resulting in
 * Working and/or Suspended durations for activities and Busy durations for
 * resources.
 * <p>
 * Warnings are logged if the log or trace does not conform to the lifecycle and
 * the result will be undetermined. E.g. a trace with start, then schedule, then
 * complete event. (This is not in the lifecycle.)
 * <p>
 * For the calculation of resource durations, warnings are logged if resources
 * occur in unexpected places. E.g. a resource in a suspend event which is
 * different from the resource which is busy with the activity.
 * <p>
 * See the individual plug-ins documentation for parameters and return values of
 * the plug-ins, and the org.processmining.plugins.duration.Durations.Duration
 * and documentation and for deriving classes for the content of the durations.
 * 
 * @author Wiebe E. Nauta (wiebenauta@gmail.com)
 */
public class CalculateDurations {

	public static final String unnamedTraceID = "unknown";

	//	private PluginContext context;
	private ByteArrayOutputStream errors;

	/**
	 * Calculates activity durations for a log, see class documentation for the
	 * durations.
	 * 
	 * @param context
	 *            The Plug-in Context.
	 * @param log
	 *            The log for which the activity durations are to be calculated.
	 * @return An object array with (1) a map mapping trace names to maps of
	 *         durations, and (2) a ByteArrayOutputStream containing warnings or
	 *         null if no warnings were generated.
	 */
	@Plugin(name = "Calculate activity durations for log", parameterLabels = { "Event Log" }, returnLabels = {
			"Map of log activity durations", "Map of log activity durations (Errors during calculation...)" }, returnTypes = {
			Map.class, ByteArrayOutputStream.class }, userAccessible = true, help = "Calculate activity durations for log")
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Wiebe E. Nauta", email = "wiebenauta@gmail.com")
	public static Object[] calculateActivityDurations(PluginContext context, XLog log) {
		CalculateDurations CalculateDurations = new CalculateDurations(context);
		Map<String, Map<DurationID, Duration>> map = CalculateDurations.calculateActivityDurations(log);
		ByteArrayOutputStream errors = CalculateDurations.getErrors();

		Object[] result;
		if (errors.size() == 0)
			result = new Object[] { map, null };
		else
			result = new Object[] { map, errors };
		
		return result;
	}

	/**
	 * Calculates resource durations for a log, see class documentation for the
	 * durations.
	 * 
	 * @param context
	 *            The Plug-in Context.
	 * @param log
	 *            The log for which the resource durations are to be calculated.
	 * @return An object array with (1) a map mapping trace names to maps of
	 *         durations, and (2) a ByteArrayOutputStream containing warnings or
	 *         null if no warnings were generated.
	 */
	@Plugin(name = "Calculate resource durations for log", parameterLabels = { "Event Log" }, returnLabels = {
			"Map of log resource durations", "Map of log resource durations (Errors during calculation...)" }, returnTypes = {
			Map.class, ByteArrayOutputStream.class }, userAccessible = true, help = "Calculate resource durations for log")
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Wiebe E. Nauta", email = "wiebenauta@gmail.com")
	public static Object[] calculateResourceDurations(PluginContext context, XLog log) {
		CalculateDurations CalculateDurations = new CalculateDurations(context);
		Map<String, Map<DurationID, Duration>> map = CalculateDurations.calculateResourceDurations(log);
		ByteArrayOutputStream errors = CalculateDurations.getErrors();

		Object[] result;
		if (errors.size() == 0)
			result = new Object[] { map, null };
		else
			result = new Object[] { map, errors };

		return result;
	}

	/**
	 * Calculates activity durations for a trace, see class documentation for
	 * the durations.
	 * 
	 * @param context
	 *            The Plug-in Context.
	 * @param trace
	 *            The trace for which the activity durations are to be
	 *            calculated.
	 * @return An object array with (1) a map of durations, and (2) a
	 *         ByteArrayOutputStream containing warnings or null if no warnings
	 *         were generated.
	 */
	@Plugin(name = "Calculate activity durations for trace", parameterLabels = { "Trace" }, returnLabels = {
			"Map of trace activity durations", "Map of trace activity durations (Errors during calculation...)" }, returnTypes = {
			Map.class, ByteArrayOutputStream.class }, userAccessible = true, help = "Calculate activity durations for trace")
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Wiebe E. Nauta", email = "wiebenauta@gmail.com")
	public static Object[] calculateActivityDurations(PluginContext context, XTrace trace) {
		CalculateDurations CalculateDurations = new CalculateDurations(context);
		Map<DurationID, Duration> map = CalculateDurations.calculateActivityDurations(trace);
		ByteArrayOutputStream errors = CalculateDurations.getErrors();

		Object[] result;
		if (errors.size() == 0)
			result = new Object[] { map, null };
		else
			result = new Object[] { map, errors };

		return result;
	}

	/**
	 * Calculates resource durations for a trace, see class documentation for
	 * the durations.
	 * 
	 * @param context
	 *            The Plug-in Context.
	 * @param trace
	 *            The trace for which the resource durations are to be
	 *            calculated.
	 * @return An object array with (1) a map of durations, and (2) a
	 *         ByteArrayOutputStream containing warnings or null if no warnings
	 *         were generated.
	 */
	@Plugin(name = "Calculate resource durations for trace", parameterLabels = { "Trace" }, returnLabels = {
			"Map of trace resource durations", "Map of trace resource durations (Errors during calculation...)" }, returnTypes = {
			Map.class, ByteArrayOutputStream.class }, userAccessible = true, help = "Calculate resource durations for trace")
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Wiebe E. Nauta", email = "wiebenauta@gmail.com")
	public static Object[] calculateResourceDurations(PluginContext context, XTrace trace) {
		CalculateDurations CalculateDurations = new CalculateDurations(context);
		Map<DurationID, Duration> map = CalculateDurations.calculateResourceDurations(trace);
		ByteArrayOutputStream errors = CalculateDurations.getErrors();

		Object[] result;
		if (errors.size() == 0)
			result = new Object[] { map, null };
		else
			result = new Object[] { map, errors };

		return result;
	}

	private CalculateDurations(PluginContext context) {
		//		this.context = context;
		errors = new ByteArrayOutputStream();
	}

	public ByteArrayOutputStream getErrors() {
		return errors;
	}

	public Map<String, Map<DurationID, Duration>> calculateActivityDurations(XLog log) {
		Map<String, Map<DurationID, Duration>> logMap = new HashMap<String, Map<DurationID, Duration>>();
		XConceptExtension conceptE = XConceptExtension.instance();

		try {
			errors.write("Time\tSeverity\tTrace ID\tEvent No.\tEvent name\tEvent instance\tMessage".getBytes());
		} catch (IOException e) {
		}

		for (XTrace trace : log) {
			String traceID = conceptE.extractName(trace);
			Map<DurationID, Duration> traceMap = calculateActivityDurations(trace);
			logMap.put(traceID, traceMap);
		}
		return logMap;
	}

	public Map<String, Map<DurationID, Duration>> calculateResourceDurations(XLog log) {
		Map<String, Map<DurationID, Duration>> map = new HashMap<String, Map<DurationID, Duration>>();
		XConceptExtension conceptE = XConceptExtension.instance();

		try {
			errors.write("Time\tSeverity\tTrace ID\tEvent No.\tEvent name\tEvent instance\tMessage".getBytes());
		} catch (IOException e) {
		}

		for (XTrace trace : log) {
			String traceID = conceptE.extractName(trace);
			Map<DurationID, Duration> traceMap = calculateResourceDurations(trace);
			map.put(traceID, traceMap);
		}
		return map;
	}

	public Map<DurationID, Duration> calculateActivityDurations(XTrace trace) {

		XConceptExtension conceptE = XConceptExtension.instance();
		XLifecycleExtension lifecycleE = XLifecycleExtension.instance();
		XTimeExtension timeE = XTimeExtension.instance();

		Map<DurationID, Duration> actMap = new HashMap<DurationID, Duration>();
		List<Duration> actList = new ArrayList<Duration>();

		// trace id for logging purposes
		String traceID = conceptE.extractName(trace);

		if (traceID == null || "".equals(traceID.trim())) {
			traceID = unnamedTraceID;
			log(traceID, "", "", "", "trace is incomplete: " + XConceptExtension.KEY_NAME
					+ " missing, set to \"unknown\"");
		}

		// event counter for logging purposes 
		Integer eventNo = 0;

		// check whether event the instance is always present, otherwise give one single error
		boolean instanceAttributeAlwaysPresent = true;

		for (XEvent event : trace) {

			eventNo++;

			String name = conceptE.extractName(event);
			String transition = lifecycleE.extractTransition(event);
			Date time = timeE.extractTimestamp(event);

			if (name == null || transition == null || time == null) {
				log(traceID, eventNo.toString(), name, "", "event is incomplete: "
						+ (name == null ? XConceptExtension.KEY_NAME + " " : "")
						+ (transition == null ? XLifecycleExtension.KEY_TRANSITION + " " : "")
						+ (time == null ? XTimeExtension.KEY_TIMESTAMP + " " : "") + "missing");
				continue;
			}

			String instance = conceptE.extractInstance(event);

			if (instanceAttributeAlwaysPresent && instance == null) {
				log(traceID, eventNo.toString(), name, instance, "event is incomplete: "
						+ XConceptExtension.KEY_INSTANCE + " missing");
				instanceAttributeAlwaysPresent = false;
			}

			ActivityDurationID actDurID;
			ActivityDuration actDur;

			// variable to verify that there are uninterrupted (active) durations at the time of an abort
			int activeDurations = 0;

			StandardModel transType = StandardModel.decode(transition);

			switch (transType) {
				case AUTOSKIP :
					break;
				case SCHEDULE :
					actDurID = new ActivityDurationID(ActivityDurationType.WAITING, name, instance);
					actDur = new ActivityDuration(ActivityDurationType.WAITING, name, instance, time);
					startDuration(actDurID, actDur, actMap, actList, time, traceID, eventNo.toString(), name, instance,
							transType);
					break;
				case MANUALSKIP :
				case WITHDRAW :
					activeDurations = 0;
					actDurID = new ActivityDurationID(ActivityDurationType.WAITING, name, instance);
					if (actMap.containsKey(actDurID)) {
						cancelDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance, transType);
						activeDurations++;
					}
					if (activeDurations == 0)
						log(traceID, eventNo.toString(), name, instance, "unexpected " + transType.getEncoding()
								+ " event found");
					break;
				case ASSIGN :
					break;
				case REASSIGN :
					break;
				case START :
					actDurID = new ActivityDurationID(ActivityDurationType.WAITING, name, instance);
					if (actMap.containsKey(actDurID))
						completeDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance, transType);
					actDurID = new ActivityDurationID(ActivityDurationType.WORKING, name, instance);
					actDur = new ActivityDuration(ActivityDurationType.WORKING, name, instance, time);
					startDuration(actDurID, actDur, actMap, actList, time, traceID, eventNo.toString(), name, instance,
							transType);
					break;
				case SUSPEND :
					actDurID = new ActivityDurationID(ActivityDurationType.SUSPENDED, name, instance);
					if (actMap.containsKey(actDurID))
						continueDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance, transType);
					else {
						actDur = new ActivityDuration(ActivityDurationType.SUSPENDED, name, instance, time);
						startDuration(actDurID, actDur, actMap, actList, time, traceID, eventNo.toString(), name,
								instance, transType);
					}
					actDurID = new ActivityDurationID(ActivityDurationType.WORKING, name, instance);
					interruptDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance, transType);
					break;
				case RESUME :
					actDurID = new ActivityDurationID(ActivityDurationType.SUSPENDED, name, instance);
					interruptDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance, transType);
					actDurID = new ActivityDurationID(ActivityDurationType.WORKING, name, instance);
					continueDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance, transType);
					break;
				case COMPLETE :
					actDurID = new ActivityDurationID(ActivityDurationType.SUSPENDED, name, instance);
					if (actMap.containsKey(actDurID)) {
						actDur = (ActivityDuration) actMap.get(actDurID);
						if (actDur.state == DurationState.EXECUTING)
							// the suspended duration should be interrupted, else log this warning
							log(traceID, eventNo.toString(), name, instance, "unexpected " + transType.getEncoding()
									+ " event found");
					}
					actDurID = new ActivityDurationID(ActivityDurationType.WAITING, name, instance);
					if (actMap.containsKey(actDurID)) {
						completeDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance, transType);
					}
					actDurID = new ActivityDurationID(ActivityDurationType.WORKING, name, instance);
					if (actMap.containsKey(actDurID)) {
						completeDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance, transType);
					}
					break;
				case ATE_ABORT :
					activeDurations = 0;
					actDurID = new ActivityDurationID(ActivityDurationType.SUSPENDED, name, instance);
					if (actMap.containsKey(actDurID)) {
						actDur = (ActivityDuration) actMap.get(actDurID);
						if (actDur.state == DurationState.EXECUTING) {
							cancelDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance,
									transType);
							activeDurations++;
						}
					}
					actDurID = new ActivityDurationID(ActivityDurationType.WORKING, name, instance);
					actDur = (ActivityDuration) actMap.get(actDurID);
					if (actDur.state == DurationState.EXECUTING) {
						cancelDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance, transType);
						activeDurations++;
					}
					if (activeDurations == 0)
						log(traceID, eventNo.toString(), name, instance, "unexpected " + transType.getEncoding()
								+ " event found");
					break;
				case PI_ABORT :
					activeDurations = 0;
					actDurID = new ActivityDurationID(ActivityDurationType.WAITING, name, instance);
					if (actMap.containsKey(actDurID)) {
						actDur = (ActivityDuration) actMap.get(actDurID);
						if (actDur.startTime.equals(actDur.endTime)) {
							cancelDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance,
									transType);
							activeDurations++;
						}
					}
					actDurID = new ActivityDurationID(ActivityDurationType.SUSPENDED, name, instance);
					if (actMap.containsKey(actDurID)) {
						actDur = (ActivityDuration) actMap.get(actDurID);
						if (actDur.state == DurationState.EXECUTING) {
							cancelDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance,
									transType);
							activeDurations++;
						}
					}
					actDurID = new ActivityDurationID(ActivityDurationType.WORKING, name, instance);
					if (actMap.containsKey(actDurID)) {
						actDur = (ActivityDuration) actMap.get(actDurID);
						if (actDur.state == DurationState.EXECUTING) {
							cancelDuration(actDurID, actMap, time, traceID, eventNo.toString(), name, instance,
									transType);
							activeDurations++;
						}
					}
					if (activeDurations == 0)
						log(traceID, eventNo.toString(), name, instance, "unexpected " + transType.getEncoding()
								+ " event found");
					break;
				case UNKNOWN :
					log(traceID, eventNo.toString(), name, instance,
							"lifecycle is not in the XES standard lifecycle model: " + transition);
					break;
				default :
					log(traceID, eventNo.toString(), name, instance,
							"lifecycle is not in the XES standard lifecycle model: " + transition);
					break;
			}
		}
		// the interrupted state is only for calculation, set to executing
		for (Duration duration : actList)
			if (duration.state == DurationState.INTERRUPTED)
				duration.state = DurationState.EXECUTING;
		return actMap;
	}

	public Map<DurationID, Duration> calculateResourceDurations(XTrace trace) {

		XConceptExtension conceptE = XConceptExtension.instance();
		XLifecycleExtension lifecycleE = XLifecycleExtension.instance();
		XTimeExtension timeE = XTimeExtension.instance();
		XOrganizationalExtension orgE = XOrganizationalExtension.instance();

		Map<DurationID, Duration> resMap = new HashMap<DurationID, Duration>();
		List<Duration> resList = new ArrayList<Duration>();

		// map for finding the last resource which was involved in the activity
		Map<ActivityID, String> lastResMap = new HashMap<ActivityID, String>();

		// trace id for logging purposes
		String traceID = conceptE.extractName(trace);

		if (traceID == null || "".equals(traceID.trim())) {
			traceID = unnamedTraceID;
			log(traceID, "", "", "", "trace is incomplete: " + XConceptExtension.KEY_NAME
					+ " missing, set to \"unknown\"");
		}

		// event counter for logging purposes 
		Integer eventNo = 0;

		// check whether event the instance is always present, otherwise give one single error
		boolean instanceAttributeAlwaysPresent = true;

		for (XEvent event : trace) {

			eventNo++;

			String name = conceptE.extractName(event);
			String transition = lifecycleE.extractTransition(event);
			Date time = timeE.extractTimestamp(event);

			if (name == null || transition == null || time == null) {
				log(traceID, eventNo.toString(), name, "", "event is incomplete: "
						+ (name == null ? XConceptExtension.KEY_NAME + " " : "")
						+ (transition == null ? XLifecycleExtension.KEY_TRANSITION + " " : "")
						+ (time == null ? XTimeExtension.KEY_TIMESTAMP + " " : "") + "missing", MessageLevel.ERROR);
				continue;
			}

			String instance = conceptE.extractInstance(event);

			if (instanceAttributeAlwaysPresent && instance == null) {
				log(traceID, eventNo.toString(), name, instance, "event is incomplete: "
						+ XConceptExtension.KEY_INSTANCE + " missing");
				instanceAttributeAlwaysPresent = false;
			}

			String resource = orgE.extractResource(event);
			if (resource == null)
				resource = orgE.extractRole(event);
			if (resource == null)
				resource = orgE.extractGroup(event);

			ActivityID actID = new ActivityID(name, instance);

			String lastRes = lastResMap.get(actID);

			ResourceDurationID resDurID;
			ResourceDuration resDur;

			StandardModel transType = StandardModel.decode(transition);

			switch (transType) {
				case AUTOSKIP :
					resource = null;
					break;
				case SCHEDULE :
					// a schedule event with resource is treated as an assign  
					if (resource == null && lastRes != null) {
						lastResMap.remove(actID);
					}
					if (resource != null && !resource.equals(lastRes)) {
						resDurID = new ResourceDurationID(ResourceDurationType.ASSIGNED, name, instance, resource);
						resDur = new ResourceDuration(ResourceDurationType.ASSIGNED, name, instance, resource, time);
						startDuration(resDurID, resDur, resMap, resList, time, traceID, eventNo.toString(), name,
								instance, transType);
					}
					if (lastRes != null && !lastRes.equals(resource)) {
						resDurID = new ResourceDurationID(ResourceDurationType.ASSIGNED, name, instance, lastRes);
						completeDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance, transType);
					}
					break;
				case MANUALSKIP :
					if (resource != null && !resource.equals(lastRes))
						log(traceID, eventNo.toString(), name, instance, "unexpected resource " + resource + " in "
								+ transType.getEncoding() + " event found");
					// use the last resource if available instead of a new given one
					if (lastRes != null) {
						resDurID = new ResourceDurationID(ResourceDurationType.ASSIGNED, name, instance, lastRes);
						// change the duration type of the last assigned duration to allocated and end duration 
						if (resMap.containsKey(resDurID)) {
							resDur = (ResourceDuration) resMap.get(resDurID);
							resMap.remove(resDurID);
							resDurID = new ResourceDurationID(ResourceDurationType.ALLOCATED, name, instance, lastRes);
							resDur.type = ResourceDurationType.ALLOCATED;
							resMap.put(resDurID, resDur);
							completeDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance,
									transType);
						}
					}
					break;
				case WITHDRAW :
					if (resource != null && !resource.equals(lastRes))
						log(traceID, eventNo.toString(), name, instance, "unexpected resource " + resource + " in "
								+ transType.getEncoding() + " event found");
					// use the last resource if available instead of a new given one
					if (lastRes != null) {
						resDurID = new ResourceDurationID(ResourceDurationType.ASSIGNED, name, instance, lastRes);
						cancelDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance, transType);
					}
					break;
				case ASSIGN :
				case REASSIGN :
					// assign and reassign are handled the same way
					if (resource == null)
						log(traceID, eventNo.toString(), name, instance, "unexpected resource ommission in "
								+ transType.getEncoding() + " event found");
					if (resource == null && lastRes != null)
						// activity is explicitly assigned no resource, so remove last resource from map
						lastResMap.remove(actID);
					if (resource != null && !resource.equals(lastRes)) {
						resDurID = new ResourceDurationID(ResourceDurationType.ASSIGNED, name, instance, resource);
						if (resMap.containsKey(resDurID))
							continueDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance,
									transType);
						else {
							resDur = new ResourceDuration(ResourceDurationType.ASSIGNED, name, instance, resource, time);
							startDuration(resDurID, resDur, resMap, resList, time, traceID, eventNo.toString(), name,
									instance, transType);
						}
					}
					if (lastRes != null && !lastRes.equals(resource)) {
						resDurID = new ResourceDurationID(ResourceDurationType.ASSIGNED, name, instance, lastRes);
						completeDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance, transType);
					}
					break;
				case START :
					if (lastRes != null) {
						resDurID = new ResourceDurationID(ResourceDurationType.ASSIGNED, name, instance, lastRes);
						if (resMap.containsKey(resDurID)) {
							// change the duration type of the last assigned duration to allocated and end duration 
							resDur = (ResourceDuration) resMap.get(resDurID);
							resMap.remove(resDurID);
							resDurID = new ResourceDurationID(ResourceDurationType.ALLOCATED, name, instance, lastRes);
							resDur.type = ResourceDurationType.ALLOCATED;
							resMap.put(resDurID, resDur);
							completeDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance,
									transType);
						}
					}
					if (resource != null && lastRes != null && !resource.equals(lastRes)) {
						log(traceID, eventNo.toString(), name, instance, "unexpected resource " + resource + " in "
								+ transType.getEncoding() + " event found");
					}
					if (resource == null && lastRes != null)
						resource = lastRes;
					if (resource != null) {
						resDurID = new ResourceDurationID(ResourceDurationType.BUSY, name, instance, resource);
						resDur = new ResourceDuration(ResourceDurationType.BUSY, name, instance, resource, time);
						startDuration(resDurID, resDur, resMap, resList, time, traceID, eventNo.toString(), name,
								instance, transType);
					}
					break;
				case SUSPEND :
					if (resource != null && !resource.equals(lastRes))
						log(traceID, eventNo.toString(), name, instance, "unexpected resource " + resource + " in "
								+ transType.getEncoding() + " event found");
					// use the last resource if available instead of a new given one
					if (lastRes != null)
						resource = lastRes;
					if (resource != null) {
						resDurID = new ResourceDurationID(ResourceDurationType.BUSY, name, instance, resource);
						interruptDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance,
								transType);
					}
					break;
				case RESUME :
					if (resource != null && !resource.equals(lastRes)) {
						log(traceID, eventNo.toString(), name, instance, "unexpected resource " + resource + " in "
								+ transType.getEncoding() + " event found");
					}
					// use the last resource if available instead of a new given one
					if (lastRes != null)
						resource = lastRes;
					if (resource != null) {
						resDurID = new ResourceDurationID(ResourceDurationType.BUSY, name, instance, resource);
						continueDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance, transType);
					}
					break;
				case COMPLETE :
					if (resource != null && lastRes != null && !resource.equals(lastRes)) {
						log(traceID, eventNo.toString(), name, instance, "unexpected resource " + resource + " in "
								+ transType.getEncoding() + " event found");
					}
					// use the last resource if available instead of a new given one
					if (lastRes != null)
						resource = lastRes;
					if (resource != null) {
						resDurID = new ResourceDurationID(ResourceDurationType.ASSIGNED, name, instance, resource);
						if (resMap.containsKey(resDurID)) {
							// change the duration type of the last assigned duration to allocated and end duration 
							resDur = (ResourceDuration) resMap.get(resDurID);
							resMap.remove(resDurID);
							resDurID = new ResourceDurationID(ResourceDurationType.ALLOCATED, name, instance, resource);
							resDur.type = ResourceDurationType.ALLOCATED;
							resMap.put(resDurID, resDur);
							completeDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance,
									transType);
						}
						resDurID = new ResourceDurationID(ResourceDurationType.BUSY, name, instance, resource);
						if (resMap.containsKey(resDurID))
							completeDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance,
									transType);
					}
					break;
				case ATE_ABORT :
					if (resource != null && !resource.equals(lastRes))
						log(traceID, eventNo.toString(), name, instance, "unexpected resource " + resource + " in "
								+ transType.getEncoding() + " event found");
					// use the last resource if available instead of a new given one
					if (lastRes != null)
						resource = lastRes;
					if (resource != null) {
						resDurID = new ResourceDurationID(ResourceDurationType.BUSY, name, instance, resource);
						if (resMap.containsKey(resDurID)) {
							resDur = (ResourceDuration) resMap.get(resDurID);
							if (resDur.state == DurationState.EXECUTING)
								cancelDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance,
										transType);
						}
					}
					break;
				case PI_ABORT :
					if (resource != null && !resource.equals(lastRes))
						log(traceID, eventNo.toString(), name, instance, "unexpected resource " + resource + " in "
								+ transType.getEncoding() + " event found");
					// use the last resource if available instead of a new given one
					if (lastRes != null)
						resource = lastRes;
					if (resource != null) {
						resDurID = new ResourceDurationID(ResourceDurationType.ASSIGNED, name, instance, resource);
						if (resMap.containsKey(resDurID)) {
							resDur = (ResourceDuration) resMap.get(resDurID);
							if (resDur.startTime.equals(resDur.endTime))
								cancelDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance,
										transType);
						}
						// no need to check for ResourceDurationType.ALLOCATED
						// all ResourceDurationType.ALLOCATED are ended after they are created
						resDurID = new ResourceDurationID(ResourceDurationType.BUSY, name, instance, resource);
						if (resMap.containsKey(resDurID)) {
							resDur = (ResourceDuration) resMap.get(resDurID);
							if (resDur.state == DurationState.EXECUTING)
								cancelDuration(resDurID, resMap, time, traceID, eventNo.toString(), name, instance,
										transType);
						}
					}
					break;
				case UNKNOWN :
					log(traceID, eventNo.toString(), name, instance,
							"lifecycle is not in the XES standard lifecycle model: " + transition);
					break;
				default :
					log(traceID, eventNo.toString(), name, instance,
							"lifecycle is not in the XES standard lifecycle model: " + transition);
					break;
			}
			if (resource != null)
				lastResMap.put(actID, resource);
		}
		// the interrupted state is only for calculation, set to executing
		for (Duration duration : resList)
			if (duration.state == DurationState.INTERRUPTED)
				duration.state = DurationState.EXECUTING;
		return resMap;
	}

	private void startDuration(DurationID durationID, Duration duration, Map<DurationID, Duration> map,
			List<Duration> list, Date time, String traceID, String eventNo, String name, String instance,
			StandardModel transType) {
		if (!map.containsKey(durationID)) {
			// temporarily use the end time as last started or continued time for duration accumulation
			duration.endTime = time;
			duration.state = DurationState.EXECUTING;
			map.put(durationID, duration);
			list.add(duration);
		} else {
			log(traceID, eventNo.toString(), name, instance, "unexpected " + transType.getEncoding() + " event found");
		}
	}

	private void completeDuration(DurationID durationID, Map<DurationID, Duration> map, Date time, String traceID,
			String eventNo, String name, String instance, StandardModel transType) {
		Duration duration = map.get(durationID);
		if (duration == null) {
			log(traceID, eventNo.toString(), name, instance, "unexpected " + transType.getEncoding() + " event found");
		} else {
			// accumulate the duration so far in the duration variable
			duration.duration += time.getTime() - duration.endTime.getTime();
			duration.endTime = time;
			duration.state = DurationState.COMPLETED;
			map.put(durationID, duration);
		}
	}

	private void cancelDuration(DurationID durationID, Map<DurationID, Duration> map, Date time, String traceID,
			String eventNo, String name, String instance, StandardModel transType) {
		completeDuration(durationID, map, time, traceID, eventNo.toString(), name, instance, transType);
		if (map.containsKey(durationID)) {
			Duration duration = map.get(durationID);
			duration.state = DurationState.CANCELED;
		}
	}

	private void interruptDuration(DurationID durationID, Map<DurationID, Duration> map, Date time, String traceID,
			String eventNo, String name, String instance, StandardModel transType) {
		Duration duration = map.get(durationID);
		if (duration == null) {
			log(traceID, eventNo.toString(), name, instance, "unexpected " + transType.getEncoding() + " event found");
		} else {
			// accumulate the duration so far in the duration variable
			duration.duration += time.getTime() - duration.endTime.getTime();
			// set the end time as the interrupted time in case of completion or abortion of the interrupting duration
			duration.endTime = time;
			duration.state = DurationState.INTERRUPTED;
			map.put(durationID, duration);
		}
	}

	private void continueDuration(DurationID durationID, Map<DurationID, Duration> map, Date time, String traceID,
			String eventNo, String name, String instance, StandardModel transType) {
		Duration duration = map.get(durationID);
		if (duration == null) {
			log(traceID, eventNo.toString(), name, instance, "unexpected " + transType.getEncoding() + " event found");
		} else {
			// temporarily use the end time as last started or continued time for duration accumulation
			duration.endTime = time;
			duration.state = DurationState.EXECUTING;
			map.put(durationID, duration);
		}
	}

	private void log(String traceID, String eventNo, String name, String instance, String message) {
		log(traceID, eventNo.toString(), name, instance, message, MessageLevel.WARNING);
	}

	private void log(String traceID, String eventNo, String name, String instance, String message, MessageLevel level) {
		log(traceID + "\t" + eventNo + "\t" + name + "\t" + instance + "\t" + message, level);
	}

	private void log(String message, MessageLevel level) {
		//		context.log(message, level);
		if (level == MessageLevel.DEBUG || level == MessageLevel.TEST)
			return;
		String now = (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")).format(Calendar.getInstance().getTime());
		String messageOut = now + "\t" + level.getLongName() + "\t" + message + "\n";
		try {
			errors.write(messageOut.getBytes());
		} catch (IOException e) {
		}
	}
}
