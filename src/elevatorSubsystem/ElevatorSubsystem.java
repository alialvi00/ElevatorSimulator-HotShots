package elevatorSubsystem;

import input.Reader;
import scheduler.*;

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
    }

    @Override
    public void run() {
    	
        while(counter < Reader.getLineCounter()){

            scheduledRequestsRequest = buf.recieveFromScheduler("elevator");
            System.out.println(Thread.currentThread().getName() + " has pulled " + scheduledRequestsRequest + " from Scheduler.");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }

            System.out.println(Thread.currentThread().getName() + " is sending " + scheduledRequestsRequest + " to Scheduler.");
            scheduledRequestsRequest.setSubsystem("elevator");
            buf.sendToScheduler(scheduledRequestsRequest, "elevator");

            counter++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e){
                e.printStackTrace();
            }

        }
    }
    

    /**
     * @return is elevator moving
     */
    public boolean isMoving() {
        return moving;
    }


    /**
     * @param moving elevator to moving or not
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
     * @param status elevator doors to open or closed
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


}
