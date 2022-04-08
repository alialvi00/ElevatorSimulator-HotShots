package elevatorStates;

import elevatorSubsystem.Elevator;
import elevatorSubsystem.ElevatorRequest;

import java.util.Random;

/**
 * class that puts elevator in the state of MovingDown
 */
public class MovingDown extends ElevatorState {

    public MovingDown(Elevator elevator) {
        super(elevator);
    }

    /**
     * decrements the elevator current floor
     */
    public void enterState() {
        elevator.setCurrentFloor(elevator.getCurrentFloor() - 1);
        System.out.println("Elevator " + elevator.returnID() + "  is moving one floor down");
        
        //startTime()
        //simulating elevator moving
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        //stopTime()
    }

    /**
     * returns the state the elevator should be in depending on the received request
     * @param request type ElevatorRequest
     * @return ElevatorState
     */
    public ElevatorState updateState(ElevatorRequest request){
    	if(request.getFailure()) {
    		elevator.setFailure();
        	return new Failure(elevator);
        }
        elevator.setElevatorDoors(request.getIsDoorOpen());
        elevator.setMotor(request.getIsMotorOn());
        elevator.setPickedUp(request.isPickedUp());
        if (elevator.isMotorOn()){
            elevator.setDirection(request.getElevDirection());

            //want to emulate a 20% chance for the elevator doors to be stuck open
            Random rand = new Random();
            //random number from 0-99
            int randomNumber = rand.nextInt(100);

            if (randomNumber >= 99) {
                //elevator had a major failure and is stuck between floors
            	try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    System.exit(-1);
                }
            }
            return new MovingDown(elevator);
        } else {
            return new Stationary(elevator);
        }
    }

}
