package elevatorSubsystem;

import input.Reader;
import scheduler.*;

import java.util.ArrayList;

abstract class State {

    protected ElevatorSubsystem elevatorSubsystem;

    static State current, stationary, movingUp, movingDown;

    public State(ElevatorSubsystem elevator){
        this.elevatorSubsystem = elevator;
    }

    abstract void enterState();
    abstract void updateState();
}

/**
 * this class describes the elevator in a stationary state
 */
class Stationary extends State{

    public Stationary(ElevatorSubsystem elevatorSubsystem){
        super(elevatorSubsystem);
    }

    void enterState() {
        if (elevatorSubsystem.isMotorOn()){
            elevatorSubsystem.setElevatorDoors(true); //open door
            elevatorSubsystem.setMotor(false); //turn off motor
        }
        System.out.println("Elevator is at a stationary state");
        elevatorSubsystem.updateRequest();
    }

    void updateState(){
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
class MovingUp extends State{

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
                Thread.sleep(1000);
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
class MovingDown extends State{

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


public class ElevatorSubsystem implements Runnable {

    private Scheduler buf;
    private SchedulerRequest scheduledRequestsRequest;
    private int counter;
    /** The motor that handles moving between elevator floors*/
    private boolean motor;

    /** The elevator doors to be opened or closed */
    private boolean elevatorDoors;

    /**To check if the up direction lamp is on */
    private boolean upDirectionLamp;

    /**To check if the down direction lamp is on */
    private boolean downDirectionLamp;

    /**Button to check if the elevator is moving */
    private boolean moving;

    /**Static count of the number of elevators instantiated**/
    private static int numOfElevators = 0;

    
    public ElevatorSubsystem(Scheduler buf){
        this.buf = buf;
        this.counter = 0;
        motor = false;
        moving = false;
        upDirectionLamp = false;
        downDirectionLamp = false;
        numOfElevators++;
        scheduledRequestsRequest = new SchedulerRequest();
    }

    @Override
    public void run() {
        //initiating all the states
        State.stationary = new Stationary(this);
        State.movingUp = new MovingUp(this);
        State.movingDown = new MovingDown(this);
        State.current = State.stationary;   //we start at stationary

        //how this loop runs and exists is temporary for now
        //until we agree on the proper way
        //setting a timer for testing purposes
        long start = System.currentTimeMillis();
        long end = start + 30*1000;
        while (System.currentTimeMillis() < end) {
            State.current.enterState();
            State.current.updateState();
        }
    }
    

    /**
     * @return is elevator moving
     */
    public boolean isMoving() {
        return moving;
    }


    /**
     * @param moving elevator to moving (true) or not (false)
     */
    public void setMoving(boolean moving) {
        this.moving = moving;
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


    /**
     * Getter method to check if the up direction lamp is on
     * @return
     */
    public boolean isUpDirectionLamp() {
        return upDirectionLamp;
    }

    /**
     * Getter method to check if the down direction lamp is on
     * @return
     */
    public boolean isDownDirectionLamp() {
        return downDirectionLamp;
    }

    /**
     * This is a static counter for the number of instances of elevators made so the floor subsystem knows how many elevators we have.
     * We can keep track by incrementing the numOfElevators by 1 every time the constructor is called (i.e a new instance is made)
     * @return numOfElevators
     */
    public static int getNumOfElevators() {
        return numOfElevators;
    }

    /**
     * this method updates
     */
    public void updateRequest(){
        this.scheduledRequestsRequest = buf.recieveFromScheduler("elevator");
        System.out.println(Thread.currentThread().getName() + " has pulled " + scheduledRequestsRequest + " from Scheduler.");
    }

    /**
     * returns the request
     * @return of type SchedulerRequest
     */
    public SchedulerRequest getRequests(){
        return scheduledRequestsRequest;
    }

    /**
     * this method sends new data to scheduler
     * @param data of type SchedulerRequest
     */
    public void sendToScheduler(SchedulerRequest data){
        this.buf.sendToScheduler(data, "elevator");
        this.buf.elevatorArrived("place holder", data.getCurrentFloor(), data.getDestinationFloor());
        System.out.println(Thread.currentThread().getName() + " is sending " + scheduledRequestsRequest + " to Scheduler.");
    }

}
