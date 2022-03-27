package floorSubsystem;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalTime;

import utils.Timer;

/**
 * This class hold important data that needs to be sent 
 * to the Scheduler in order for it to be assigned to 
 * the optimal elevator. 
 * @author Akaash Kapoor
 *
 */
public class FloorRequest implements Serializable{
	
	/**Unique ID for the serializable object.*/
	private static final long serialVersionUID = 123L;
	
	/**Represents elevator direction.*/
	private String elevatorDirection = "";
	
	/**Represents the passenger's destination floor.*/
	private int destinationFloor;
	
	/**Stores the passenger's arrival time.*/
	private String arrivalTime;
	
	/**Represents the pickup floor of the passenger.*/
	private int ID = -1;
	
	private boolean lastRequest;
	
	/**
	 * Constructor for initializing the Floor Request to Scheduler.
	 * @param ID - The pickup floor of the passenger
	 * @param arrivalTime - The time the passenger arrived at the floor. 
	 * @param elevatorDirection - The direction that the passenger wants to go.
	 * @param destinationFloor - The floor the passenger wants to go to.
	 * @param isLastRequest - Boolean value that helps scheduler determine if this is the last request for floor. 
	 */
	public FloorRequest(int ID, String arrivalTime, String elevatorDirection, int destinationFloor, boolean isLastRequest) {
		
		this.setElevatorDirection(elevatorDirection);
		this.setDestinationFloor(destinationFloor);	
		this.setArrivalTime(arrivalTime);
		this.ID = ID;
		this.lastRequest = isLastRequest;
	}
	
    
    
    /**
     * Method that converts the floor request class to bytes.
     * In order to send it via DatagramSocket.
     */
    public byte[] byteRepresentation() {
    	
    	try {
    		
    		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
    		ObjectOutputStream objStream = new ObjectOutputStream(new BufferedOutputStream(outStream));
    		
    		//Ensures object output stream is cleared before converting class to bytes. 
    		objStream.flush();
    		objStream.writeObject(this);
    		objStream.flush();
    		
    		return outStream.toByteArray();
    		
    	}catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		
    }
	
	/**
	 * Getter method for returning elevator direction. 
	 * @return the elevatorDirection
	 */
	public String getElevatorDirection() {
		return elevatorDirection;
	}


	/**
	 * Setter method for setting elevator direction. 
	 * @param elevatorDirection the elevatorDirection to set
	 */
	public void setElevatorDirection(String elevatorDirection) {
		this.elevatorDirection = elevatorDirection;
	}


	/**
	 * Getter method for getting destination floor. 
	 * @return the destinationFloor
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}

	/**
	 * Setter method for setting destination floor. 
	 * @param destinationFloor the destinationFloor to set
	 */
	public void setDestinationFloor(int destinationFloor) {
		this.destinationFloor = destinationFloor;
	}


	/**
	 * Get the passenger arrivate time to the elevators.
	 * @return the arrivalTime
	 */
	public String getArrivalTime() {
		return arrivalTime;
	}
	
	/**
	 * Checks to see if the ID is empty. 
	 * @return
	 */
	public boolean isEmpty() {
		return ID == -1;

	}


	/**
	 * Set the passenger arrival time to the elevators. 
	 * @param arrivalTime the arrivalTime to set
	 */
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}


	/**
	 * Gets the boolean status of the request if it is last or not. 
	 * @return the lastRequest
	 */
	public boolean isLastRequest() {
		return lastRequest;
	}
	
	
	/**
	 * Getter function to return the pickup floor.
	 * @return pickup floor. 
	 */
	public int getID() {
		return ID;
	}

}
