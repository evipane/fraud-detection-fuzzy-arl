package org.processmining.plugins.duration;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.processmining.plugins.duration.DurationStates.DurationState;
import org.processmining.plugins.duration.DurationTypes.ActivityDurationType;
import org.processmining.plugins.duration.DurationTypes.DurationType;

/**
 * 
 * 
 * @author Wiebe E. Nauta (wiebenauta@gmail.com)
 */
public class Durations {

	public static class Duration {

		public DurationType type;
		public DurationState state;
		public String eventName;

		/**
		 * event instance may be null if it is not used in the events
		 */
		public String eventInstance;

		public Date startTime;
		public Date endTime;

		/**
		 * caution: duration is not necessarily endTime - startTime because
		 * interruptions can occur
		 */
		public Long duration;

		public Duration(DurationType type, String eventName, String eventInstance, Date startTime) {
			this.type = type;
			this.state = DurationState.EXECUTING;
			this.eventName = eventName;
			this.eventInstance = eventInstance;
			this.startTime = startTime;
			this.endTime = null;
			this.duration = 0l;
		}
	}

	public static class ActivityDuration extends Duration {
		public ActivityDuration(ActivityDurationType type, String eventName, String eventInstance, Date startTime) {
			super(type, eventName, eventInstance, startTime);
		}

		public String toString() {
			return String.format("%1$-10s", type).substring(0, 10) + "\t"
					+ String.format("%1$-10s", state).substring(0, 10) + "\t"
					+ String.format("%1$-30s", eventName + " " + eventInstance).substring(0, 30) + "\tfrom\t"
					+ dateFormat.format(startTime) + "\tto\t" + dateFormat.format(endTime) + "\tfor\t"
					+ formatDuration(duration);
		}
	}

	public static class ResourceDuration extends Duration {
		public String resource;

		public ResourceDuration(DurationType type, String eventName, String eventInstance, String resource,
				Date startTime) {
			super(type, eventName, eventInstance, startTime);
			this.resource = resource;
		}

		public String toString() {
			return resource + "\t" + String.format("%1$-10s", type).substring(0, 10) + "\t"
					+ String.format("%1$-10s", state).substring(0, 10) + "\t"
					+ String.format("%1$-30s", eventName + " " + eventInstance).substring(0, 30) + "\tfrom\t"
					+ dateFormat.format(startTime) + "\tto\t" + dateFormat.format(endTime) + "\tfor\t"
					+ formatDuration(duration);
		}
	}

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	private static String formatDuration(Long duration) {
		String format = "";
		format += duration >= 86400000 ? duration / 86400000 + "d" : "";
		format += duration >= 3600000 ? String.format("%02d", (duration % 86400000) / 3600000) + ":" : "00:";
		format += duration >= 60000 ? String.format("%02d", (duration % 3600000) / 60000) + ":" : "00:";
		format += duration >= 1000 ? String.format("%02d", (duration % 60000) / 1000) + "." : "00:";
		format += duration >= 0 ? String.format("%03d", (duration % 1000)) : "000";
		return format;
	}
}
