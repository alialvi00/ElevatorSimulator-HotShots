package elevatorStates;

import elevatorSubsystem.*;

/**
 * this class describes the elevator in a stationary state
 */
public class Stationary extends ElevatorState {

    public Stationary(Elevator elevator) {
        super(elevator);
    }

    /**
     * turns off the motor, opens the door, and prints out a message
     */
    public void enterState() {
        System.out.println("Elevator " + elevator.returnID() + " is at a stationary state at floor " + elevator.getCurrentFloor());

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}