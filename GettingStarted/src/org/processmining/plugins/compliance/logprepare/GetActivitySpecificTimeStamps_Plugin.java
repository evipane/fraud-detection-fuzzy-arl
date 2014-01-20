/**
 * 
 */
package org.processmining.plugins.compliance.logprepare;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(name = "Get Activity-Specific Timestamps",
	returnLabels = { "Event Log" },
	returnTypes = { XLog.class },
	parameterLabels = {"Event Log"}, 
	help = "Copies and renames the time-stamp attribute of each event based on the activity name.", userAccessible = true
	)
public class GetActivitySpecificTimeStamps_Plugin {
	
	@UITopiaVariant(
			affiliation = UITopiaVariant.EHV,
			author = "Dirk Fahland",
			email = "d.fahland@tue.nl",
			pack = "Compliance")
	@PluginVariant(variantLabel = "Get Activity-Specific Timestamps", requiredParameterLabels = { 0 })
	public XLog replayLog(PluginContext context, XLog originalLog) {
		
		XFactory f = XFactoryRegistry.instance().currentDefault();
		
		XLog newLog = f.createLog(originalLog.getAttributes());
		for (XTrace t : originalLog) {
			XTrace tNew = f.createTrace(t.getAttributes());
			
			for (XEvent e : t) {
				XEvent eNew = f.createEvent(e.getAttributes());
				
				String eventName = XConceptExtension.instance().extractName(eNew);
				String eventLifeCycle = XLifecycleExtension.instance().extractTransition(eNew);
				Date timeStamp = XTimeExtension.instance().extractTimestamp(eNew);
				
				if (timeStamp != null) {
					String timeStamp_newName = eventName+"+"+eventLifeCycle+"_time";
					
					XAttributeTimestamp timeStamp_new = f.createAttributeTimestamp(timeStamp_newName, timeStamp, XTimeExtension.instance());
					eNew.getAttributes().put(timeStamp_newName, timeStamp_new);
				}
				
				tNew.add(eNew);
			}
			newLog.add(tNew);
		}
		
		DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		String timeString = dateFormat.format(date);
		
		context.getFutureResult(0).setLabel(
				XConceptExtension.instance().extractName(originalLog)
					+ " (activity-specific timestamps  @"+ timeString+")");

		
			//context.log("replay is not performed because not enough parameter is submitted");
			//context.getFutureResult(0).cancel(true);
			//return null;
		
		return newLog;
	}
	


}
