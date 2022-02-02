package scheduler;
import java.util.*;


/**
 * This class represents the Scheduler subsystem. Its main purpose is to act as a buffer and allow communication
 * between Floor subsystem and Elevator subsystem
 * 
 * @author Ali Alvi
 * 
 */
public class Scheduler implements Runnable{
	
	//These arrayLists are the data structure used to transfer data between floor and elevator subsystems 
	public ArrayList<String> floorData;
	public ArrayList<String> elevatorData;
	
	/**
	 * Constructor for the scheduler class 
	 * 
	 */
	public Scheduler() {
		//this.numOfFloors = numOfFloors;
		//this.numOfElevators = numOfElevators;
		
		//Initialize the data structure for transferring data
		floorData = new ArrayList<>();
		elevatorData = new ArrayList<>();
	}
	
	/**
	 * Synchronized method to receive floor data from floor subsystem 
	 * @param floorDataReceived is an arrayList consisting of floor data
	 * @return boolean value to indicate if the transfer was successful
	 */
	public synchronized boolean sendFloorData(ArrayList<String> floorDataReceived) {
		
		
		//Check if the floor data is empty
		while(floorDataReceived.isEmpty()) {
			
			//If so, then wait
			try {
				wait();
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//If not empty, store the floor data in elevatorData to be sent to elevator subsystem 
		elevatorData = floorDataReceived;
		System.out.println("SCHEDULER: Received Floor Data from Floor");
		notifyAll(); //Notify all the threads
		return true; //return true because the data was stored successfully
	}
	
	
	/**
	 * Synchronized method to send updated floor data back to floor subsystem
	 * 
	 * @return ArrayList that contains the updated floor data
	 */
	public synchronized ArrayList<String> getFloorData() {
		
		//If floor data is empty then wait to receive it
		while(floorData.isEmpty()) {
			try {
				wait();
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("SCHEDULER: Sending Updated Floor Data from Scheduler");
		return floorData; //return the updated floor data
	}
	
	/**
	 * Synchronized method that receives elevator data from the elevator subsystem
	 * @param elevatorDataReceived is the elevator data to be sent to the floor subsystem
	 * @return boolean value to indicate if the data was successfully stored
	 */
	public synchronized boolean sendElevatorData(ArrayList<String> elevatorDataReceived) {
		while(elevatorDataReceived.isEmpty()) { //If data received is empty
			try {
				wait(); //wait to receive data
			}
			catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//Store the elevator data in the floor data to be sent to the floor subsystem
		floorData = elevatorDataReceived;
		System.out.println("SCHEDULER: Received Elevator Data from Elevator");
		notifyAll(); //Notify all the threads
		return true; //return true since the data was stored successfully
	}
	
	/**
	 * Synchronized method to send updated elevator data back to the elevator subsystem
	 * @return ArrayList that contains the updated data structure
	 */
	public synchronized ArrayList<String> getElevatorData() {
		while(elevatorData.isEmpty()) { //If elevator data is empty, then wait to receive it
			try {
				wait();
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Create a temporary ArrayList to hold updated elevator data, this is done so we can clear the actual elevator data before
		//returning the elevator data
		System.out.println("SCHEDULER: Sending Floor Data from Scheduler to Elevator.");
		notifyAll(); //Notify all the threads
		
		return elevatorData; //return the updated elevator data
	}
	
	/**
	 * This method is overriding the run method from Runnable interface and will be used in later iterations
	 */
	public void run() {}
}
