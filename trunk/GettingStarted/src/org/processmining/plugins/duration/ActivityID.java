package org.processmining.plugins.duration;

/**
 * 
 * 
 * @author Wiebe E. Nauta (wiebenauta@gmail.com)
 */
public class ActivityID {
	
	public String eventName;
	public String eventInstance;
	
	public ActivityID(String eventName, String eventInstance) {
		this.eventName = eventName;
		this.eventInstance = eventInstance;
	}
	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((eventInstance == null) ? 0 : eventInstance.hashCode());
		result = prime * result + ((eventName == null) ? 0 : eventName.hashCode());
		return result;
	}
	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityID other = (ActivityID) obj;
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
		return true;
	}
}
