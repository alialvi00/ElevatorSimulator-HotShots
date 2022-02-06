package elevatorSubsystem;
import floorSubsystem.*;
import scheduler.*;

import java.util.ArrayList;


/**
 * This class represents the Elevator subsystem. Its main purpose is to serve as a client that makes calls to the Scheduler
 * and sends info back to the scheduler
 *
 * @author Areeb Haq
 * @version 1.1
 *
 */
public class ElevatorSubsystem implements Runnable{
	
	/** Scheduler object that will handle message passing from one subsystem to another.*/
	private Scheduler scheduler;
	
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

	/**This arrayLists is the data structure used to transfer data between scheduler and elevator subsystems  */
	private ArrayList<String> dataFromScheduler = new ArrayList<>();


	/**
	 * Constructor for elevator subsystem, it will take the scheduler as a parameter to use as the server
	 * @param scheduler
	 */
	public ElevatorSubsystem(Scheduler scheduler){
		this.scheduler = scheduler;
		motor = false;
		moving = false;
		upDirectionLamp = false;
		downDirectionLamp = false;
		numOfElevators++;
	}
	
	
	/**
	 * @return is elevator moving
	 */
	public boolean isMoving() {
		return moving;
	}


	/**
	 * @param set elevator to moving or not
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
	 * @param set elevator doors to open or closed
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
	 * @param motor the motor to set
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
	 * The runnable override method that will retrieve data from the scheduler and return it to the scheduler.
	 */
	@Override
	public void run() {
		//while running set moving to true
		setMoving(true);
		
		/*Run through until the end of file has been reached*/
		while(!FloorSubsystem.end_of_file) {
			
			dataFromScheduler = scheduler.getElevatorData(); //continuously make calls to get data from scheduler
			
			synchronized (scheduler) { 
				
				if(!dataFromScheduler.isEmpty()) { //we will send elevator data back to the scheduler only when we received data
					System.out.println("ELEVATOR: Received Floor Data from Scheduler: " + dataFromScheduler);
					System.out.println("ELEVATOR: Sending Elevator Data to Scheduler."); //once the data is received it will send it back to the scheduler.
					
					/* Call a synchronized send function that is defined in the scheduler class. */
					scheduler.sendElevatorData(dataFromScheduler);
				}
			}
			
			/*Suspend the thread for 1000ms*/
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
