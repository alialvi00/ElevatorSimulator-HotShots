package elevatorSubsystem;

import utils.*;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalTime;

public class ElevatorRequest implements Serializable{

	private static final long serialVersionUID = 1L;
	private final int ID;
	private int pickupFloor;
	private int elevatorCurrentFloor;
	private int destinationFloor;
	private String elevatorDirection;
	private Timer timer;
	
	public ElevatorRequest(int ID) {
		this.ID = ID;
		timer = new Timer();
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
	
	public void setDestinationFloor(int destinationFloor) {
		this.destinationFloor = destinationFloor;
	}
	
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	public void setElevDirection(String elevatorDirection) {
		this.elevatorDirection = elevatorDirection;
	}
	
	public String getElevDirection() {
		return elevatorDirection;
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
