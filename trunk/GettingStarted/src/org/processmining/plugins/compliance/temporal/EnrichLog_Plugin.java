package org.processmining.plugins.compliance.temporal;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.compliance.temporal.TemporalComplianceRequirement.EventTimeStampInit;

@Plugin(name = "Enrich log for temporal compliance checking",
    parameterLabels = { "log", "temporal compliance requirement" }, 
	returnLabels = { "enriched log" },
	returnTypes = { XLog.class },
	help = "Enrich events with timestamp attributes required for temporal compliance checking.", userAccessible = true)
public class EnrichLog_Plugin {

	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Enrich log for temporal compliance checking", requiredParameterLabels = { 0 })
	public XLog enrichLog(UIPluginContext context, XLog log) {
		return enrichLog(context, log, null);			
	}
	
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Enrich log for temporal compliance checking", requiredParameterLabels = { 0, 1 })
	public XLog enrichLog(PluginContext context, XLog originalLog, TemporalComplianceRequirement req) {
		
		XFactory f = XFactoryRegistry.instance().currentDefault();
		
		XLog logNew = f.createLog(originalLog.getAttributes());
		for (XTrace trace : originalLog) {
			XTrace traceNew = f.createTrace(trace.getAttributes());
			
			// give each event that has a valid time stamp a second time-stamp attribute prefixed by its name
			for (int i=0; i<trace.size(); i++) {
				XEvent e = trace.get(i);
				XEvent eNew = f.createEvent(e.getAttributes());
				
				Date timeStamp = XTimeExtension.instance().extractTimestamp(eNew);
				
				if (timeStamp != null) {
					XEventClass eClass = req.allEventClasses.getClassOf(eNew);
					String timeStampName = getEventSpecificTimestampName(eClass);
					setEventSpecificTimestamp(eNew, timeStampName, timeStamp, f);
	
					if (req != null) setTimeStampInitsForOtherEvents(eNew, timeStamp, eClass, req, f);
				}
				traceNew.add(eNew);
			}

			logNew.add(traceNew);
		}
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String timeString = dateFormat.format(date);
		
		String enrichedLogName = XConceptExtension.instance().extractName(originalLog)
				+ " (enriched for temporal compliance  @"+ timeString+")";
		context.getFutureResult(0).setLabel(enrichedLogName);
		context.addConnection(new EnrichLog_Connection("Connection from "+enrichedLogName+" to "+XConceptExtension.instance().extractName(originalLog), originalLog, logNew));
		
			//context.log("replay is not performed because not enough parameter is submitted");
			//context.getFutureResult(0).cancel(true);
			//return null;
		
		return logNew;
	}
	
	private void setTimeStampInitsForOtherEvents(XEvent eNew, Date timeStamp, XEventClass eClass, TemporalComplianceRequirement req, XFactory f) {
		for (EventTimeStampInit initSpec : req.eventTimeStampInitSpec) {
			if (initSpec.initBy.equals(eClass)) {
				String initFor_timeStampName = getEventSpecificTimestampName(initSpec.initFor);
				setEventSpecificTimestamp(eNew, initFor_timeStampName, timeStamp, f);
			}
		}
	}
	
	/**
	 * @param e
	 * @return attribute name for a timestamp of this event, being formatted as
	 *         "EVENTCLASSID_time"
	 */
	private static String getEventSpecificTimestampName(XEventClass e) {
		String timeStampName = e.getId()+"_time";
		return timeStampName;
	}
	
	private static void setEventSpecificTimestamp(XEvent e, String timeStampName, Date timeStamp, XFactory f) {
		XAttributeTimestamp timeStamp_new = f.createAttributeTimestamp(timeStampName, timeStamp, XTimeExtension.instance());
		e.getAttributes().put(timeStampName, timeStamp_new);
	}
	
	protected static XLog cancel(PluginContext context, String message) {
		System.out.println("[Enrich log]: "+message);
		context.log(message);
		context.getFutureResult(0).cancel(true);
		return null;
	}
}
