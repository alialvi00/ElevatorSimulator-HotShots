package elevatorSubsystem;

import elevatorStates.*;
import elevatorView.ElevatorView;

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
    
    private ElevatorView view;

    private boolean isPickedUp = false;


    private boolean isFailure = false;

    
    public Elevator(int id, ElevatorSubsystem subsystem, ElevatorView view){
    	
    	this.view = view;
    	
        this.id = id;
        this.motor = false;
        this.view.updateMotorStatus(motor);
        this.elevatorDoors = false;
        this.view.updateDoorStatus(elevatorDoors);
        currentFloor = 1;
        this.view.updateCurrentFloor(currentFloor);
        direction = "";
        this.view.updateElevatorDirection(direction);
        this.subsystem = subsystem;
    }

    /**
     * waits for the elevator subsystem to assign it a request
     */
    @Override
    public void run(){
        //initiating all the states
        ElevatorState stationary = new Stationary(this);
        ElevatorState current = stationary;   //we start at stationary

        while(true){
            current.enterState();
            
            if(isFailure) {
            	return;
            }
            //creating request to send to scheduler
            ElevatorRequest request = createRequest();
            
            //send to scheduler
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
    
    public void outputToGUI(String message) {
    	view.writeToConsole(message);
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
    	view.updateDoorStatus(status);
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
    	view.updateMotorStatus(status);
        this.motor = status;
    }


    public int getCurrentFloor(){return currentFloor;}


    public ElevatorRequest getExecutingRequest(){
        return executingRequest;
    }

    /**
     * Used by the subsystem to assign the elevator a request
     * @param request
     */
    public void setExecutingRequest(ElevatorRequest request){
        this.executingRequest = request;
    }

    public void setCurrentFloor(int floor){
    	view.updateCurrentFloor(floor);
        currentFloor = floor;
    }

    public void setDirection(String direction){
    	view.updateElevatorDirection(direction);
    	this.direction = direction;
    }

    public String getDirection(){return direction;}

    public boolean getPickedUp() {return isPickedUp;}
    
    public void setPickedUp(boolean isPickedUp){
    	view.updatePassengerStatus(isPickedUp);
    	this.isPickedUp = isPickedUp;
    }

    /**
     * creates and returns an ElevatorRequest object based on elevator's state
     * @return
     */
    public ElevatorRequest createRequest(){
        ElevatorRequest request = new ElevatorRequest(id, currentFloor, elevatorDoors, motor);
        request.setPickedUp(isPickedUp);
        if(isFailure) {
        	request.setFailure();
        }
        if (isMotorOn()){
            request.setElevDirection(getDirection());
        }

        return request;
    }


    /**
     * Getter for failure
     * @return
     */
    public boolean isFailure() {
        return isFailure;
    }

    /**
     * Setter for failure
     * @return
     */
    public void setFailure(){
    	isFailure = true;
    	view.updateFaultStatus(true);
    }
}

