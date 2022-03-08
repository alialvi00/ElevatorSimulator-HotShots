package floorSubsystem;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class FloorRequest {
	
	private String elevatorDirection;
	
	private String arrivalTime;
	
	private int pickupFloor;
	
	private int destinationFloor;
	
	private byte[] requestInBytes;
	
	public FloorRequest(String arrivalTime, int pickupFloor, String elevatorDirection, int destinationFloor) {
		
		this.setArrivalTime(arrivalTime);
		this.setPickupFloor(pickupFloor);
		this.setElevatorDirection(elevatorDirection);
		this.setDestinationFloor(destinationFloor);
		this.setRequestInBytes(new byte[20]);
		this.setRequestInBytes(convertRequestToBytes());
		
		
	}
	
	/**
     * Method that converts the floor request to bytes.
     * In order to send it via DatagramSocket.
     */
    public byte[] convertRequestToBytes() {
    	
    	ByteArrayOutputStream byteArr = new ByteArrayOutputStream();
    	
    	byteArr.writeBytes(arrivalTime.getBytes());
    	byteArr.write(pickupFloor);
    	try {
			byteArr.write(elevatorDirection.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
    	byteArr.write(destinationFloor);
    	
    	return byteArr.toByteArray();
    	
    	
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

	/**
	 * @return the requestInBytes
	 */
	public byte[] getRequestInBytes() {
		return requestInBytes;
	}

	/**
	 * @param requestInBytes the requestInBytes to set
	 */
	public void setRequestInBytes(byte[] requestInBytes) {
		this.requestInBytes = requestInBytes;
	}
	
}
