package elevatorStates;

import elevatorSubsystem.*;
import java.util.Random;

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
    	
    	String message;
    	
    	if(!elevator.getPickedUp()) {
    		message = "Elevator " + elevator.returnID() + " is at a stationary state at floor " + elevator.getCurrentFloor();
    		System.out.println(message);
    		elevator.outputToGUI(message);
    		
    	} else {
    		message = "Elevator " + elevator.returnID() + " is at a stationary state at floor " + elevator.getCurrentFloor() + " and is picking up passengers.";
    		System.out.println(message);
    		elevator.outputToGUI(message);
    	}
        
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * returns the state the elevator should be in depending on the received request
     * there is a 30% cahnce that the elevators doors get stuck open
     * @param request type ElevatorRequest
     * @return ElevatorState
     */
    public ElevatorState updateState(ElevatorRequest request){
    	
        //If the elevator is in a stationary state and the doors need to be closed
        if(elevator.areElevatorDoorsOpen() && !request.getIsDoorOpen()){
            //want to emulate a 30% chance for the elevator doors to be stuck open
            Random rand = new Random();
            //random number from 0-99
            int randomNumber = rand.nextInt(100);

            if (randomNumber >= 70){
                //door is stuck open so stay in stationary
            	
            	String update = "Elevator " + elevator.returnID() + " doors are stuck open at floor " + elevator.getCurrentFloor();
                System.out.println(update);
                elevator.outputToGUI(update);
                return new Stationary(elevator);
            }
        }
        elevator.setElevatorDoors(request.getIsDoorOpen());
        elevator.setMotor(request.getIsMotorOn());
        elevator.setPickedUp(request.isPickedUp());
        if (elevator.isMotorOn()){
            elevator.setDirection(request.getElevDirection());
            if(elevator.getDirection().equalsIgnoreCase("Up")){
                return new MovingUp(elevator);
            } else {
                return new MovingDown(elevator);
            }
        } else {
            return this;
        }
    }

}
