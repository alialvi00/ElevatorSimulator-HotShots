package scheduler;

import scheduler.SchedulerState.Event;

/**
 * Class which sets the state of the state machine and defaults it to be idle.
 * @author Ali Alvi Raj Sandhu
 * @version 1.0
 *
 */

public class SchedulerStateMachine {

	private SchedulerState state;
	
	/**
	 * Constructor which defaults the state to idle.
	 */
	public SchedulerStateMachine() {
		this.state = SchedulerState.IDLE;
	}
	
	/**
	 * method which transitions the state of scheduler.
	 * @param e event which causes state transition.
	 */
	
	public void transition(Event e) {
		this.state = this.state.transition(e);
	}
	
	/**
	 * Method to get the current state of the scheduler.
	 * @return current state of scheduler.
	 */
	
	public SchedulerState getCurrentState() {
		return this.state;
	}
}
