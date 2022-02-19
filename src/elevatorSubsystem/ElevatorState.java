package elevatorSubsystem;

import scheduler.SchedulerRequest;

public abstract class ElevatorState {
    protected ElevatorSubsystem elevatorSubsystem;

    static ElevatorState current, stationary, movingUp, movingDown;

    public ElevatorState(ElevatorSubsystem elevator){
        this.elevatorSubsystem = elevator;
    }

    abstract void enterState();
    abstract void updateState();
}

/**
 * this class describes the elevator in a stationary state
 */
class Stationary extends ElevatorState{

    public Stationary(ElevatorSubsystem elevatorSubsystem){
        super(elevatorSubsystem);
    }

    void enterState() {
        if (elevatorSubsystem.isMotorOn()){
            elevatorSubsystem.setElevatorDoors(true); //open door
            elevatorSubsystem.setMotor(false); //turn off motor
        }
        System.out.println("Elevator is at a stationary state");
    }

    void updateState(){
    	//while there are no new requests, stay in the stationary state
    	while(elevatorSubsystem.getRequestsQueue().isEmpty()) {
    		//sleep in between checking for new requests
    		try {
				Thread.sleep(500);
			} 
            catch (InterruptedException e){
                e.printStackTrace();
                System.exit(-1);
            }
    	}
    	
        //request received from scheduler
        SchedulerRequest request = elevatorSubsystem.getRequests();

        if (request.getDestinationFloor() == request.getCurrentFloor()){
            current = stationary;
            return;
        }
        else if (request.getDestinationFloor() > request.getCurrentFloor()){
            current = movingUp;
            return;
        }
        else if (request.getDestinationFloor() < request.getCurrentFloor()){
            current = movingDown;
            return;
        }
    }
}

/**
 * class that puts elevator in the state of MovingUp
 */
class MovingUp extends ElevatorState{

    public MovingUp(ElevatorSubsystem elevatorSubsystem){
        super(elevatorSubsystem);
    }

    void enterState(){
        if (!elevatorSubsystem.isMotorOn()){
            elevatorSubsystem.setElevatorDoors(false);
            elevatorSubsystem.setMotor(true); //turn on motor
        }
        System.out.println("Elevator is moving one floor up");

        //simulating elevator moving
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    void updateState(){
        SchedulerRequest request = elevatorSubsystem.getRequests();

        //update current floor
        request.setCurrentFloor(request.getCurrentFloor() + 1);

        if (request.getDestinationFloor() > request.getCurrentFloor()){
            current = movingUp;
            return;
        }
        if (request.getDestinationFloor() == request.getCurrentFloor()){
            //sending new data to scheduler
            elevatorSubsystem.sendToScheduler(request);

            //wait for potential response
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            current = stationary;
            return;
        }
    }
}

/**
 * class that puts elevator in the state of MovingDown
 */
class MovingDown extends ElevatorState{

    public MovingDown(ElevatorSubsystem elevatorSubsystem){
        super(elevatorSubsystem);
    }

    void enterState(){
        if (!elevatorSubsystem.isMotorOn()){
            elevatorSubsystem.setElevatorDoors(false); //close doors
            elevatorSubsystem.setMotor(true); //turn on motor
        }
        System.out.println("Elevator is moving one floor down");

        //simulating elevator moving
        try {
            Thread.sleep(5000);
        }
        catch (InterruptedException e){
            e.printStackTrace();
            System.exit(-1);
        }
    }

    void updateState(){
        SchedulerRequest request = elevatorSubsystem.getRequests();

        //update current floor
        request.setCurrentFloor(request.getCurrentFloor() - 1);

        if (request.getDestinationFloor() < request.getCurrentFloor()){
            current = movingDown;
            return;
        }
        if (request.getDestinationFloor() == request.getCurrentFloor()){
            //sending new data to scheduler
            elevatorSubsystem.sendToScheduler(request);

            //waiting for potential response
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }
            current = stationary;
            return;
        }
    }
}
