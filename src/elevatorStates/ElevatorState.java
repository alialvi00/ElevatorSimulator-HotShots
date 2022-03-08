package elevatorStates;

import elevatorSubsystem.ElevatorSubsystem;

public abstract class ElevatorState {
    protected ElevatorSubsystem elevatorSubsystem;

    public static ElevatorState current;
    public static ElevatorState stationary;
    public static ElevatorState movingUp;
    public static ElevatorState movingDown;

    public ElevatorState(ElevatorSubsystem elevator){
        this.elevatorSubsystem = elevator;
    }

    public abstract void enterState();
    public abstract void updateState();
}

