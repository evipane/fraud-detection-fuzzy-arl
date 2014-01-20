package org.processmining.plugins.duration;

/**
 * 
 * 
 * @author Wiebe E. Nauta (wiebenauta@gmail.com)
 */
public class DurationTypes { 
	
	public interface DurationType {
	}

	public enum ResourceDurationType implements DurationType {
		ASSIGNED("assigned"), ALLOCATED("allocated"), BUSY("busy"), UNKNOWN("unknown");

		private final String type;

		private ResourceDurationType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public static ResourceDurationType decode(String type) {
			type = type.trim().toLowerCase();
			for (ResourceDurationType t : ResourceDurationType.values()) {
				if (t.type.equals(type)) {
					return t;
				}
			}
			return ResourceDurationType.UNKNOWN;
		}
	}

	public enum ActivityDurationType implements DurationType {
		WAITING("waiting"), WORKING("working"), SUSPENDED("suspended"), UNKNOWN("unknown");

		private final String type;

		private ActivityDurationType(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public static ActivityDurationType decode(String type) {
			type = type.trim().toLowerCase();
			for (ActivityDurationType t : ActivityDurationType.values()) {
				if (t.type.equals(type)) {
					return t;
				}
			}
			return ActivityDurationType.UNKNOWN;
		}
	}
}
