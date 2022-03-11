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
    public ElevatorState updateState(ElevatorRequest request){
        elevator.setElevatorDoors(request.getIsDoorOpen());
        elevator.setMotor(request.getIsMotorOn());
        if (elevator.isMotorOn()){
            elevator.setDirection(request.getElevDirection());
            if(elevator.getDirection().equalsIgnoreCase("up")){
                return new MovingUp(elevator);
            } else {
                return new MovingDown(elevator);
            }
        } else {
            return new Stationary(elevator);
        }
    }
}