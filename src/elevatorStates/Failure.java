package elevatorStates;

import elevatorSubsystem.Elevator;
import elevatorSubsystem.ElevatorRequest;

public class Failure extends ElevatorState{

    public Failure(Elevator elevator){super(elevator);}

    public void enterState(){
    	/*
    	 * startTime()
    	 * sleep(3000)
    	 * stopTime()
    	 * 
    	 */
        System.out.println("Elevator " + elevator.returnID() + "  underwent hard fault and has been decommissioned.");
    }

    //will not call updateState if the state is Failure
    @Override
    public ElevatorState updateState(ElevatorRequest request) {
        return null;
    }
}
