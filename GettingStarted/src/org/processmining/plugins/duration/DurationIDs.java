package org.processmining.plugins.duration;

import org.processmining.plugins.duration.DurationTypes.DurationType;

/**
 * 
 * 
 * @author Wiebe E. Nauta (wiebenauta@gmail.com)
 */
public class DurationIDs {

	public static class DurationID {

		public DurationType type;
		public String eventName;
		public String eventInstance;

		public DurationID(DurationType type, String eventName, String eventInstance) {
			this.type = type;
			this.eventName = eventName;
			this.eventInstance = eventInstance;
		}

		/**
		 * Eclipse-generated hashCode(), since object is used as key in Map
		 */
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((eventInstance == null) ? 0 : eventInstance.hashCode());
			result = prime * result + ((eventName == null) ? 0 : eventName.hashCode());
			result = prime * result + ((type == null) ? 0 : type.hashCode());
			return result;
		}

		/**
		 * Eclipse-generated equals(), since object is used as key in Map
		 */
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DurationID other = (DurationID) obj;
			if (eventInstance == null) {
				if (other.eventInstance != null)
					return false;
			} else if (!eventInstance.equals(other.eventInstance))
				return false;
			if (eventName == null) {
				if (other.eventName != null)
					return false;
			} else if (!eventName.equals(other.eventName))
				return false;
			if (type == null) {
				if (other.type != null)
					return false;
			} else if (!type.equals(other.type))
				return false;
			return true;
		}
	}
	
	public static class ActivityDurationID extends DurationID  {
		public ActivityDurationID(DurationType type, String eventName, String eventInstance) {
			super(type, eventName, eventInstance);
		}
	}

	public static class ResourceDurationID extends DurationID {

		public String resource;
		
		public ResourceDurationID(DurationType type, String eventName, String eventInstance, String resource) {
			super(type, eventName, eventInstance);
			this.resource = resource;
		}

		public int hashCode() {
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((resource == null) ? 0 : resource.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			ResourceDurationID other = (ResourceDurationID) obj;
			if (resource == null) {
				if (other.resource != null)
					return false;
			} else if (!resource.equals(other.resource))
				return false;
			return true;
		}
	}
}
