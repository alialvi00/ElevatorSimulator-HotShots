package elevatorSubsystem;

import input.Reader;
import scheduler.*;


import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;



public class ElevatorSubsystem implements Runnable {

    private Scheduler buf;
    private SchedulerRequest scheduledRequestsRequest;
    /** The motor that handles moving between elevator floors*/
    private boolean motor;

    /** The elevator doors to be opened or closed */
    private boolean elevatorDoors;

    /**Button to check if the elevator is moving */
    private boolean moving;

    /**Static count of the number of elevators instantiated**/
    private static int numOfElevators = 0;

    
    public ElevatorSubsystem(Scheduler buf){
        this.buf = buf;
        motor = false;
        moving = false;
        numOfElevators++;
        scheduledRequestsRequest = new SchedulerRequest();
    }

    @Override
    public void run() {

        //initiating all the states
        ElevatorState.stationary = new Stationary(this);
        ElevatorState.movingUp = new MovingUp(this);
        ElevatorState.movingDown = new MovingDown(this);
        ElevatorState.current = ElevatorState.stationary;   //we start at stationary

        //setting a timeout
        //long start = System.currentTimeMillis();
        //long end = start + 30*1000;
        while (true) {
        	ElevatorState.current.enterState();
        	ElevatorState.current.updateState();
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
        //System.out.println(Thread.currentThread().getName() + " has pulled " + scheduledRequestsRequest.toString() + " from Scheduler.");
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
        this.buf.elevatorArrived(data.getArrivalTime(), data.getCurrentFloor());
        //System.out.println(Thread.currentThread().getName() + " is sending " + scheduledRequestsRequest.toString() + " to Scheduler.");
    }
    
    /**
     * this method returns the scheduler requests queue
     * @return que of type LinkedBlockingQueue
     */
    public LinkedBlockingQueue<SchedulerRequest> getRequestsQueue() {
		return buf.getRequestQue();
	}

}
