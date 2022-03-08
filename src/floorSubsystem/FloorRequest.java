package floorSubsystem;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamClass;
import java.io.Serializable;


public class FloorRequest implements Serializable{
	
	private static final long serialVersionUID = 123L;

	private String elevatorDirection;
	
	private String arrivalTime;
	
	private int pickupFloor;
	
	private int destinationFloor;
	
	/**
	 * Constructor for initializing the Floor Request to Scheduler.
	 * @param arrivalTime
	 * @param pickupFloor
	 * @param elevatorDirection
	 * @param destinationFloor
	 */
	public FloorRequest(String arrivalTime, int pickupFloor, String elevatorDirection, int destinationFloor) {
		
		this.setArrivalTime(arrivalTime);
		this.setPickupFloor(pickupFloor);
		this.setElevatorDirection(elevatorDirection);
		this.setDestinationFloor(destinationFloor);		
		
		
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
	 * @return the arrivalTime
	 */
	public String getArrivalTime() {
		return arrivalTime;
	}

	/**
	 * @param arrivalTime the arrivalTime to set
	 */
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
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


	
}
