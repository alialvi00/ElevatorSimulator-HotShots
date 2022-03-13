package elevatorStates;

import elevatorSubsystem.Elevator;

/**
 * class that puts elevator in the state of MovingUp
 */
public class MovingUp extends ElevatorState {

    public MovingUp(Elevator elevator) {
        super(elevator);
    }

    /**
     * increments the elevator current floor
     */
    public void enterState() {
        elevator.setCurrentFloor(elevator.getCurrentFloor() + 1);
        System.out.println("Elevator " + elevator.returnID() + "  is moving one floor up");

        //simulating elevator moving
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
