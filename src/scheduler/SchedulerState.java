package scheduler;

/**
 * The following enum represents the states the scheduler can take.
 * @author Ali Alvi Raj Sandhu
 * @version 1.0
 *
 */

public enum SchedulerState {
	
	//If the state is idle switch to waiting state.
	IDLE {
		
		@Override
		public SchedulerState transition(Event e) {
			if(e == Event.RECEIVED_REQUEST) {

				return WAITING; //State is waiting.

			}
			return ERROR;
		}
	},
	
	//If the state is waiting switch to idle state.
	WAITING {

		
		@Override
		public SchedulerState transition(Event e) {
			if(e == Event.SCHEDULED_REQUEST) {
				return IDLE; //State is Idle.
			}
			return ERROR;
		}
	},
	
	//Go to error state if there is an error.
	ERROR{
		
		@Override
		public SchedulerState transition(Event e) {
			return ERROR;
		}
	};
	
	/**
	 * Abstract class which represents the state transitions for the scheduler.
	 * @param e the event transition.
	 * @return scheduler state.
	 */
	
	public abstract	SchedulerState transition(Event e);
	
	//Events which can cause a change of states.
	public static enum Event{
		RECEIVED_REQUEST,
		SCHEDULED_REQUEST
	}
}
