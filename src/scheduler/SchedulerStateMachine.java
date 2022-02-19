package scheduler;

import scheduler.SchedulerState.Event;

public class SchedulerStateMachine {

	private SchedulerState state;
	
	public SchedulerStateMachine() {
		this.state = SchedulerState.IDLE;
	}
	
	public void transition(Event e) {
		this.state = this.state.transition(e);
	}
	
	public SchedulerState getCurrentState() {
		return this.state;
	}
}
