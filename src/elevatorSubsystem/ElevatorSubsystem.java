package elevatorSubsystem;
import floorSubsystem.*;
import scheduler.*;

import java.util.ArrayList;


/**
 * This class represents the Elevator subsystem. Its main purpose is to serve as a client that makes calls to the Scheduler
 * and sends info back to the scheduler
 *
 * @author Areeb  Haq
 *
 */
public class ElevatorSubsystem implements Runnable{

	private Scheduler scheduler;

	private boolean upFloorButton;
	private boolean downFloorButton;
	private boolean upDirectionLamp;
	private boolean downDirectionLamp;


	private static int numOfElevators = 0;

	//This arrayLists is the data structure used to transfer data between scheduler and elevator subsystems 
	private ArrayList<String> dataFromScheduler = new ArrayList<>();

	//private int currentFloor;

	public ElevatorSubsystem(Scheduler scheduler){
		this.scheduler = scheduler;
		upFloorButton = false;
		downFloorButton = false;
		upDirectionLamp = false;
		downDirectionLamp = false;
		//this.currentFloor = currentFloor;
		numOfElevators++;
	}



	public boolean isUpFloorButton() {
		return upFloorButton;
	}

	public boolean isDownFloorButton() {
		return downFloorButton;
	}

	public boolean isUpDirectionLamp() {
		return upDirectionLamp;
	}

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
		while(true){
			dataFromScheduler = scheduler.getElevatorData(); //continuously make calls to get data from scheduler

			while(this.dataFromScheduler.isEmpty()){
				synchronized(scheduler) {
					try{
						wait(); //if the arraylist is empty -> no data received so wait()
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}


			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("ELEVATOR: Received Floor Data from Scheduler.");
			System.out.println("ELEVATOR: Sending Elevator Data to Scheduler.");
			scheduler.sendElevatorData(dataFromScheduler); //once the data is received it will send it back to the scheduler.
		}
	}
}
