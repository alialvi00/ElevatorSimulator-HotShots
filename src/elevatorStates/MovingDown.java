package elevatorStates;

import elevatorSubsystem.ElevatorSubsystem;
import scheduler.SchedulerRequest;

/**
 * class that puts elevator in the state of MovingDown
 */
public class MovingDown extends ElevatorState {

    public MovingDown(ElevatorSubsystem elevatorSubsystem) {
        super(elevatorSubsystem);
    }

    public void enterState() {
        if (!elevatorSubsystem.isMotorOn()) {
            elevatorSubsystem.setElevatorDoors(false); //close doors
            elevatorSubsystem.setMotor(true); //turn on motor
        }
        System.out.println("Elevator is moving one floor down");

        //simulating elevator moving
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void updateState() {
        SchedulerRequest request = elevatorSubsystem.getRequests();

        //update current floor
        request.setCurrentFloor(request.getCurrentFloor() - 1);

        if (request.getDestinationFloor() < request.getCurrentFloor()) {
            current = movingDown;
            return;
        }
        if (request.getDestinationFloor() == request.getCurrentFloor()) {
            //updating arrival time
            request.setArrivalTime("" + System.currentTimeMillis() / 1000);
            //sending new data to scheduler
            elevatorSubsystem.sendToScheduler(request);

            //waiting for potential response
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            current = stationary;
            return;
        }
    }
}
