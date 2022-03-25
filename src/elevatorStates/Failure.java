package elevatorStates;

import elevatorSubsystem.Elevator;
import elevatorSubsystem.ElevatorRequest;

public class Failure extends ElevatorState{

    public Failure(Elevator elevator){super(elevator);}

    public void enterState(){
        System.out.println("Elevator " + elevator.returnID() + "  is stuck and will be decommissioned");
    }

    //will not call updateState if the state is Failure
    @Override
    public ElevatorState updateState(ElevatorRequest request) {
        return null;
    }
}
