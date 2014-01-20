package org.processmining.plugins.duration;

/**
 * 
 * 
 * @author Wiebe E. Nauta (wiebenauta@gmail.com)
 */
public class DurationStates {

	public enum DurationState {
		COMPLETED("completed"), CANCELED("canceled"), EXECUTING("executing"), INTERRUPTED("interrupted"), UNKNOWN(
				"unknown");

		private final String state;

		private DurationState(String state) {
			this.state = state;
		}

		public String getState() {
			return state;
		}

		public static DurationState decode(String state) {
			state = state.trim().toLowerCase();
			for (DurationState t : DurationState.values()) {
				if (t.state.equals(state)) {
					return t;
				}
			}
			return DurationState.UNKNOWN;
		}
	}
}
