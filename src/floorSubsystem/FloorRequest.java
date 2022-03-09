package floorSubsystem;

import utils.Timer;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalTime;


public class FloorRequest implements Serializable{
	
	private static final long serialVersionUID = 123L;

	private String elevatorDirection;
	
	private int pickupFloor;
	
	private int destinationFloor;
	
	private int ID;
	
	private Timer timer;
	
	/**
	 * Constructor for initializing the Floor Request to Scheduler.
	 * @param arrivalTime
	 * @param pickupFloor
	 * @param elevatorDirection
	 * @param destinationFloor
	 */
	public FloorRequest(int pickupFloor, String elevatorDirection, int destinationFloor, int ID) {
		
		this.setPickupFloor(pickupFloor);
		this.setElevatorDirection(elevatorDirection);
		this.setDestinationFloor(destinationFloor);		
		this.ID = ID;
		timer = new Timer();
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
	 * @return the pickupFloor
	 */
	public int getPickupFloor() {
		return pickupFloor;
	}

	/**
	 * @param pickupFloor the pickupFloor to set
	 */
	public void setPickupFloor(int pickupFloor) {
		this.pickupFloor = pickupFloor;
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
	
	public boolean isEmpty() {
		return ID == 0;
	}
	
	public void setInitialTime() {
		timer.setInitialTime();
	}
	
	public long getInitialTime() {
		return timer.getInitialTime();
	}
	
	public void setArrivalTime() {
		timer.setArrivalTime();
	}
	
	public long getArrivalTime() {
		return timer.getArrivalTime();
	}
	
	public LocalTime getLocalTime() {
		return timer.getLocalTime();
	}
}
