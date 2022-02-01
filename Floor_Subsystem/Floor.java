package milestone_1;

import java.util.*;
import java.io.*;


/**
 * This class implements the floor subsystem of the elevator. Its main purpose
 * is to read in input data from the user to interact with the elevator subsystem. 
 * @author Akaash Kapoor
 * @version 1.0
 *
 */
public class Floor implements Runnable{
	
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
	private String[] schedulerInfo;
	
	/** The floor number that the elevator is currently on */
	private int current_floor;
	
	
	/**
	 * This constructor allows a Floor object to be constructed to a specific floor. 
	 * @param floor The floor number of a building. 
	 */
	public Floor(int floor) 
	{
		schedulerInfo = new String[4];
		current_floor = floor;		
	}
	
	
	
	public boolean getButtonLampUp() {
		return buttonLampUp;
	}
	
	public void setButtonLampUp(boolean buttonLamp) {
		buttonLampUp = buttonLamp;
	}
	
	public boolean getButtonLampDown() {
		return buttonLampDown;
	}
	
	public void setButtonLampDown(boolean buttonLamp) {
		buttonLampDown = buttonLamp;
	}
	
	public boolean getDirectionLampUp() {
		return directionLampUp;
	}
	
	public void setDirectionLampUp(boolean directionLamp) {
		directionLampUp = directionLamp;
	}
	
	public boolean getDirectionLampDown(){
		return directionLampDown;
	}
	
	public void setDirectionLampDown(boolean directionLamp) {
		directionLampDown = directionLamp;
	}
	
	public int getFloorNumber() {
		return current_floor;
	}
	
	
	/**
	 * The function executes the floor thread.
	 */
	public void run() {
		
		System.out.println("Starting Floor Subsystem Simulator.....");
		try {
			readInput();
		} catch(IOException e){System.exit(0);};
		
		
	}
	
	
	/**
	 * This method reads input file of the elevator timing and it's direction along
	 * with its floor of interest. 
	 * @throws IOException An exception thrown if no file is present to be read. 
	 */
	public void readInput() throws IOException {
		
		try {
			
			BufferedReader inputReader =  new BufferedReader(new FileReader("test.txt"));
			
			while (inputReader.ready()) 
			{
				StringTokenizer st = new StringTokenizer(inputReader.readLine(), " ");
				
				schedulerInfo[0] = st.nextToken();
				System.out.println("Passenger arrival time: " + schedulerInfo[0]);
				
				schedulerInfo[1] = st.nextToken();
				System.out.println("The floor the passenger resides on is: " + schedulerInfo[1]);
				
				schedulerInfo[2] = st.nextToken();
				
				if(schedulerInfo[2].equals("Up")) {
					setButtonLampUp(true);
					setButtonLampDown(false);
					System.out.println("Elevator's direction is up.");
				} else {
					setButtonLampUp(false);
					setButtonLampDown(true);
					System.out.println("Elevator's direction is down.");
				}
				
				schedulerInfo[3] = st.nextToken();
				System.out.println("Destination floor is: " + schedulerInfo[3]);
				System.out.println("---------------------------------");
				
				
				
				/* Call a synchronized send function that is defined in the scheduler class.
				 * Sends the timestamp, current floor, direction, and destination_floor. 
				 * 
				*/
				
				
				
				/*
				 * Call a synchronized receive function that is defined in the scheduler class. 
				 * Receives the floor of the elevator, direction lamp (on or off) , and button lamp (off)
				 */
				
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
				
			}
			
		} catch (FileNotFoundException e) {
            System.out.println("Error Opening File! ");
            System.exit(0);
        }
		
		
	}
	
	//Test class
	public static void main(String[] args) {
		Thread ground_floor = new Thread(new Floor(1), "Ground Floor");
		ground_floor.start();
	}
	
	
}
