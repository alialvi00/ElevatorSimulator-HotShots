package elevatorSubsystem;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ElevatorRequest implements Serializable{

	private final int ID;
	private final String requestDir;
	private int pickupFloor;
	private int elevatorCurrentFloor;
	private int destinationFloor;
	private String elevatorDirection;
	
	public ElevatorRequest(int ID, String requestDir ) {
		this.ID = ID;
		this.requestDir = requestDir;
	}
	
	public void setPickupFloor(int pickupFloor) {
		this.pickupFloor = pickupFloor;
	}
	
	public int getPickupFloor() {
		return pickupFloor;
	}
	
	public void setElevCurrentFloor(int elevatorCurrentFloor) {
		this.elevatorCurrentFloor = elevatorCurrentFloor;
	}
	
	public int getElevCurrentFloor() {
		return elevatorCurrentFloor;
	}
	
	public void setDestinationFloor() {
		this.destinationFloor = destinationFloor;
	}
	
	public void setElevDirection(String elevatorDirection) {
		this.elevatorDirection = elevatorDirection;
	}
	
	public String getElevDirection() {
		return elevatorDirection;
	}
	
	public String getRequestDir() {
		return requestDir;
	}
	
	public boolean isEmpty() {
		return (ID == 0 || requestDir.isEmpty());
	}
	
	public byte[] byteRepresentation() {
		
		try {
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ObjectOutputStream objectStream = new ObjectOutputStream(new BufferedOutputStream(outputStream));
			objectStream.flush();
			objectStream.writeObject(this);
			objectStream.flush();
			
			return outputStream.toByteArray();
		}
		catch(IOException ie) {
			ie.printStackTrace();
			return null;
		}
	}
	
}
