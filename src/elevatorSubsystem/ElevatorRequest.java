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
	private int ID = -1;
	private int pickupFloor;
	private int elevatorCurrentFloor;
	private int destinationFloor;
	private Timer timer;
	private boolean motorOn; //make it true when elevator in motion
	private boolean doorsOpen; //make it true when elevator gets customer and doors are open
	private boolean elevatorOnline; //always make it true when elev corresponding to ID is active
	private String elevatorStatus; //normal, updateDoor, offline
	private String schedulerStatus; //closeDoor, openDoor, up, down, continue, stop
	private String requestDir; //elevToSch or schToElev
	
	
	public ElevatorRequest(int ID) {
		this.ID = ID;
	}
	
	/**
	 * 
	 * @param ID
	 * @param elevatorStatus fill it if schToElev or null if not
	 * @param schedulerStatus fill it if elevToSch or null if not
	 * @param requestDir
	 */
	public ElevatorRequest(int ID, String elevatorStatus, String schedulerStatus, String requestDir) {
		this.ID = ID;
		this.elevatorStatus = elevatorStatus;
		this.schedulerStatus = schedulerStatus;
		this.requestDir = requestDir;
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
	
	public boolean isEmpty() {
		return ID == -1;
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
	
	public void setMotorOn() {
		motorOn = true;
	}
	
	public boolean getMotorStatus() {
		return motorOn;
	}
	
	public void setDoorOpen() {
		doorsOpen = true;
	}
	
	public boolean getDoorsStatus() {
		return doorsOpen;
	}
	
	public int getID() {
		return ID;
	}
	
	public void setElevatorStatus(String elevatorStatus) {
		this.elevatorStatus = elevatorStatus;
	}
	
	public String getElevatorStatus() {
		return elevatorStatus;
	}
	
	public void setSchStatus(String schStatus) {
		this.schedulerStatus = schStatus;
	}
	
	public String getSchStatus() {
		return schedulerStatus;
	}
	
	public String getRequestDir() {
		return requestDir;
	}
	
	public void turnElevatorOn() {
		elevatorOnline = true;
	}
	
	public boolean isElevOn() {
		return elevatorOnline;
	}

}
