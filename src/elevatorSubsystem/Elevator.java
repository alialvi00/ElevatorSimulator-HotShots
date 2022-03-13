package elevatorSubsystem;

import elevatorStates.ElevatorState;
import elevatorStates.MovingDown;
import elevatorStates.MovingUp;
import elevatorStates.Stationary;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Elevator implements Runnable{
    /** Unique elevator id*/
    private int id;

    /** The motor that handles moving between elevator floors*/
    private boolean motor;

    /** The elevator doors to be opened or closed */
    private boolean elevatorDoors;

    private int currentFloor;

    private ElevatorRequest executingRequest;

    private String direction;

    private ElevatorState state;

    private ElevatorSubsystem subsystem;

    private boolean isPickedUp = false;

    
    public Elevator(int id, ElevatorSubsystem subsystem){
        this.id = id;
        this.motor = false;
        this.elevatorDoors = false;
        currentFloor = 1;
        direction = "";
        this.subsystem = subsystem;
    }

    @Override
    public void run(){
        //initiating all the states
        ElevatorState stationary = new Stationary(this);
        ElevatorState current = stationary;   //we start at stationary

        while(true){
            current.enterState();
            //creating request to send to scheduler
            ElevatorRequest request = createRequest();
            subsystem.sendRequest(request);

            executingRequest = null; //prepare for new request

            while(executingRequest == null){
                try {
                    Thread.sleep(250);
                } catch(InterruptedException e) {
                    e.printStackTrace();
                    System.exit(1);
                }
            }

            current = current.updateState(executingRequest);
        }
    }

    public int returnID(){
        return id;
    }

    /**
     * @return Getter method to check if elevator doors are open
     */
    public boolean areElevatorDoorsOpen() {
        return elevatorDoors;
    }


    /**
     * @param status elevator doors to open (true) or closed (false)
     */
    public void setElevatorDoors(boolean status) {
        this.elevatorDoors = status;
    }


    /**
     * Getter method to check if motor is on
     * @return the motor
     */
    public boolean isMotorOn() {
        return motor;
    }

    /**
     * @param status the motor to set
     */
    public void setMotor(boolean status) {
        this.motor = status;
    }


    public int getCurrentFloor(){return currentFloor;}


    public ElevatorRequest getExecutingRequest(){
        return executingRequest;
    }

    public void setExecutingRequest(ElevatorRequest request){
        this.executingRequest = request;
    }

    public void setCurrentFloor(int floor){
        currentFloor = floor;
    }

    public void setDirection(String direction){this.direction = direction;}

    public String getDirection(){return direction;}

    public boolean getPickedUp() {return isPickedUp;}
    
    public void setPickedUp(boolean isPickedUp){this.isPickedUp = isPickedUp;}

    public ElevatorRequest createRequest(){
        ElevatorRequest request = new ElevatorRequest(id, currentFloor, elevatorDoors, motor);
        request.setPickedUp(isPickedUp);
        if (isMotorOn()){
            request.setElevDirection(getDirection());
        }

        return request;
    }

}

