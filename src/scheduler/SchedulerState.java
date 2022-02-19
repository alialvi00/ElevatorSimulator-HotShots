package scheduler;

public enum SchedulerState {
	
	IDLE {
		
		@Override
		public SchedulerState transition(Event e) {
			if(e == Event.RECEIVED_REQUEST) {

				return WAITING;

			}
			return ERROR;
		}
	},
	

	WAITING {

		
		@Override
		public SchedulerState transition(Event e) {
			if(e == Event.SCHEDULED_REQUEST) {
				return IDLE;
			}
			return ERROR;
		}
	},
	ERROR{
		
		@Override
		public SchedulerState transition(Event e) {
			return ERROR;
		}
	};
	
	
	public abstract	SchedulerState transition(Event e);
	
	public static enum Event{
		RECEIVED_REQUEST,
		SCHEDULED_REQUEST
	}
}
