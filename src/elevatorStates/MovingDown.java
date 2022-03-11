package elevatorStates;

import elevatorSubsystem.Elevator;

/**
 * class that puts elevator in the state of MovingDown
 */
public class MovingDown extends ElevatorState {

    public MovingDown(Elevator elevator) {
        super(elevator);
    }

    public void enterState() {
        elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
        System.out.println("Elevator is moving one floor down");

        //simulating elevator moving
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
