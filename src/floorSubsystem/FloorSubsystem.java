package floorSubsystem;

import java.util.*;

import scheduler.*;

import java.io.*;


/**
 * This class implements the floor subsystem of the elevator. Its main purpose
 * is to read in input data from the user to interact with the elevator subsystem. 
 * @author Akaash Kapoor
 * @version 1.1
 *
 */
public class FloorSubsystem implements Runnable{
	
	/** Determines if elevator is going up and notifies floor. */
	private boolean buttonLampUp;
	
	/** Determines if elevator is going down and notifies floor. */
	private boolean buttonLampDown;
	
	/** Lights up the up lamp. */
	private boolean directionLampUp;
	
	/** Lights up the down lamp. */
	private boolean directionLampDown;
	
	/** Simulates the arrival sensor.*/
	private boolean isElevatorClose;
	
	/** Stores information that will be sent to the scheduler. */
	private ArrayList<String> floorToScheduler;
	
	/** Stores information that will be retrieved from the scheduler.*/
	private ArrayList<String> schedulerToFloor;
	
	/** Scheduler object that will handle message passing from one subsystem to another.*/
	private Scheduler scheduler;
	
	/** The floor number that the elevator is currently on */
	private int current_floor;
	
	public static boolean end_of_file;
	
	
	/**
	 * This constructor allows a Floor object to be constructed to a specific floor. 
	 * @param floor The floor number of a building. 
	 */
	public FloorSubsystem(Scheduler scheduler, int floor) 
	{
		floorToScheduler = new ArrayList<>();
		schedulerToFloor = new ArrayList<>();
		current_floor = floor;	
		this.scheduler = scheduler;
		end_of_file = false;
	}
	
	
	/**
	 * Getter function for retrieving the up button.
	 * @return Status of up button in the form of a boolean.
	 */
	public boolean getButtonLampUp() {
		return buttonLampUp;
	}
	
	/**
	 * Setter function for configuring the up button. 
	 * @param buttonLamp The boolean status of the button lamp (on/off). 
	 */
	public void setButtonLampUp(boolean buttonLamp) {
		buttonLampUp = buttonLamp;
	}
	
	/**
	 * Getter function for retrieving the down button.
	 * @return Status of down button in the form of a boolean.
	 */
	public boolean getButtonLampDown() {
		return buttonLampDown;
	}
	
	/**
	 * Setter function for configuring the down button. 
	 * @param buttonLamp The boolean status of the button lamp (on/off). 
	 */
	public void setButtonLampDown(boolean buttonLamp) {
		buttonLampDown = buttonLamp;
	}
	
	/**
	 * Getter function for retrieving the status of lamp in the up direction. 
	 * @return Status of the lamp in the upwards direction in the form of a boolean.
	 */
	public boolean getDirectionLampUp() {
		return directionLampUp;
	}
	
	/**
	 * Setter function for configuring the lamp in the up direction. 
	 * @param directionLamp The boolean status of the lamp (on/off). 
	 */
	public void setDirectionLampUp(boolean directionLamp) {
		directionLampUp = directionLamp;
	}
	
	/**
	 * Getter function for retrieving the status of lamp in the down direction. 
	 * @return Status of the lamp in the downwards direction in the form of a boolean.
	 */
	public boolean getDirectionLampDown(){
		return directionLampDown;
	}
	
	/**
	 * Setter function for configuring the lamp in the downward direction.
	 * @param directionLamp The boolean status of the lamp (on/off). 
	 */
	public void setDirectionLampDown(boolean directionLamp) {
		directionLampDown = directionLamp;
	}
	
	/**
	 * Getter function for retrieving the floor number of the floor instance. 
	 * @return An integer representing the floor number. 
	 */
	public int getFloorNumber() {
		return current_floor;
	}
	
	
	/**
	 * The function executes the floor thread.
	 */
	@Override
	public void run() {
		
		System.out.println("Starting Simulator.....");
		try {
			readInput();
			end_of_file = true;
		} catch(IOException e){
			System.exit(0);
		};	
	}
	
	
	/**
	 * This method reads input file of the elevator timing and it's direction along
	 * with its floor of interest. 
	 * @throws IOException An exception thrown if no file is present to be read. 
	 */
	public void readInput() throws IOException {
		
		try {
			
			BufferedReader inputReader =  new BufferedReader(new FileReader("Inputs/test.txt"));
			
			while (inputReader.ready()) 
			{
				StringTokenizer st = new StringTokenizer(inputReader.readLine(), " ");
				
				//System.out.println("\n");
				
				floorToScheduler.add(st.nextToken());
				//System.out.println("\nPassenger arrival time: " + floorToScheduler.get(0));
				
				floorToScheduler.add(st.nextToken());
				//System.out.println("The floor the passenger resides on is: " + floorToScheduler.get(1));
				
				floorToScheduler.add(st.nextToken());
				
				if(floorToScheduler.get(2).equals("Up")) {
					setButtonLampUp(true);
					setButtonLampDown(false);
					//System.out.println("Elevator's direction is up.");
				} else {
					setButtonLampUp(false);
					setButtonLampDown(true);
					//System.out.println("Elevator's direction is down.");
				}
				
				floorToScheduler.add(st.nextToken());
				//System.out.println("Destination floor is: " + floorToScheduler.get(3) + "\n");
				
				
				synchronized (scheduler) {
					
					/* 
					 * Call a synchronized send function that is defined in the scheduler class.
					*/
					System.out.println("FLOOR: Sending Floor Data to Scheduler: " + floorToScheduler);
					
					if(scheduler.sendFloorData(floorToScheduler)) {
						
						/*
						 * Call a synchronized receive function that is defined in the scheduler class. 
						 */
						schedulerToFloor = scheduler.getFloorData();
						System.out.println("FLOOR: Recieved Floor Data from Scheduler: " + schedulerToFloor);
					}
				}
				
				/*Suspend the thread to in order to prepare for the next data in the text file. */
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
				floorToScheduler.clear();
				schedulerToFloor.clear();
				
			}
			
		} catch (FileNotFoundException e) {
            System.out.println("Error Opening File! ");
            System.exit(0);
        }
		
		
	}
	
	
}
