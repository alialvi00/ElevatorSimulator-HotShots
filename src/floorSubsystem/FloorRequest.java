package floorSubsystem;


import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalTime;

import utils.Timer;


public class FloorRequest implements Serializable{
	
	private static final long serialVersionUID = 123L;

	private String elevatorDirection;

	private int destinationFloor;
	
	private String arrivalTime;

	private int ID = -1;
	
	private Timer timer = new Timer();
	
	private boolean lastRequest;
	
	/**
	 * Constructor for initializing the Floor Request to Scheduler.
	 * @param arrivalTime
	 * @param pickupFloor
	 * @param elevatorDirection
	 * @param destinationFloor
	 */
	public FloorRequest(String arrivalTime, String elevatorDirection, int destinationFloor, int ID, boolean isLastRequest) {
		
		this.setElevatorDirection(elevatorDirection);
		this.setDestinationFloor(destinationFloor);	
		this.setArrivalTime(arrivalTime);
		this.ID = ID;
		timer = new Timer();
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
	 * @return the elevatorDirection
	 */
	public String getElevatorDirection() {
		return elevatorDirection;
	}


	/**
	 * @param elevatorDirection the elevatorDirection to set
	 */
	public void setElevatorDirection(String elevatorDirection) {
		this.elevatorDirection = elevatorDirection;
	}


	/**
	 * @return the destinationFloor
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}

	/**
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
	 * Get the timer duration of the arrival time. 
	 * @return the timer
	 */
	public long getTimerArrivalTime() {
		return timer.getArrivalTime();
	}

	/**
	 * Stop the timer duration for the arrival time. 
	 */
	public void setTimerArrivalTime() {
		this.timer.setArrivalTime();
	}

	/**
	 * @return the lastRequest
	 */
	public boolean isLastRequest() {
		return lastRequest;
	}
	

	public LocalTime getLocalTime() {
		return timer.getLocalTime();
	}
	
	public int getID() {
		return ID;
	}

}
