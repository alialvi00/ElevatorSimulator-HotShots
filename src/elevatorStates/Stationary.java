package elevatorStates;

import elevatorSubsystem.ElevatorSubsystem;
import scheduler.SchedulerRequest;

/**
 * this class describes the elevator in a stationary state
 */
public class Stationary extends ElevatorState {

    public Stationary(ElevatorSubsystem elevatorSubsystem) {
        super(elevatorSubsystem);
    }

    public void enterState() {
        if (elevatorSubsystem.isMotorOn()) {
            elevatorSubsystem.setElevatorDoors(true); //open door
            elevatorSubsystem.setMotor(false); //turn off motor
        }
        System.out.println("Elevator is at a stationary state");
    }

    public void updateState() {
        //while there are no new requests, stay in the stationary state
        while (elevatorSubsystem.getRequestsQueue().isEmpty()) {
            //sleep in between checking for new requests
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }

        //request received from scheduler
        elevatorSubsystem.updateRequest();
        SchedulerRequest request = elevatorSubsystem.getRequests();

        if (request.getDestinationFloor() == request.getCurrentFloor()) {
            current = stationary;
            return;
        } else if (request.getDestinationFloor() > request.getCurrentFloor()) {
            System.out.println("Elevator is currently at floor: " + request.getCurrentFloor());
            current = movingUp;
            return;
        } else if (request.getDestinationFloor() < request.getCurrentFloor()) {
            System.out.println("Elevator is currently at floor: " + request.getCurrentFloor());
            current = movingDown;
            return;
        }
    }
}
