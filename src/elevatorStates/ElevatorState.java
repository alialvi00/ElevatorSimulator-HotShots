package elevatorStates;

import elevatorSubsystem.*;

public abstract class ElevatorState {
    protected Elevator elevator;

    public static ElevatorState current;
    public static ElevatorState stationary;
    public static ElevatorState movingUp;
    public static ElevatorState movingDown;

    public ElevatorState(Elevator elevator){
        this.elevator = elevator;
    }

    public abstract void enterState();

    public abstract ElevatorState updateState(ElevatorRequest request);
}