package elevatorSubsystem;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;



public class ElevatorRequest implements Serializable{

	private static final long serialVersionUID = 1L;

	private int ID = -1;
	private int elevatorCurrentFloor;
	private String elevatorDirection;
	private boolean isDoorOpen;
	private boolean isMotorOn;

	/**
	 * This constructor will be used by the elevator subsystem
	 * @param ID of type int
	 * @param elevatorCurrentFloor of type int
	 * @param isDoorOpen boolean
	 * @param isMotorOn boolean
	 */
	public ElevatorRequest(int ID, int elevatorCurrentFloor, boolean isDoorOpen, boolean isMotorOn) {
		this.ID = ID;
		this.elevatorCurrentFloor = elevatorCurrentFloor;
		this.isDoorOpen = isDoorOpen;
		this.isMotorOn = isMotorOn;
	}

	/**
	 * this constructor will be used by the scheduler
	 * @param ID of type int
	 * @param isDoorOpen boolean
	 * @param isMotorOn boolean
	 */
	public ElevatorRequest(int ID, boolean isDoorOpen, boolean isMotorOn) {
		this.ID = ID;
		this.isDoorOpen = isDoorOpen;
		this.isMotorOn = isMotorOn;
	}

	/**
	 * converts the elevator request object into a byte array
	 * @return
	 */

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

	/**
	 * get the current floor of the elevator
	 * @return int
	 */
	public int getElevCurrentFloor() {
		return elevatorCurrentFloor;
	}

	/**
	 * set the elevator travel direction
	 * optional as elevator not always moving
	 * @param elevatorDirection pf type string ("up" or "down")
	 */
	public void setElevDirection(String elevatorDirection) {
		this.elevatorDirection = elevatorDirection;
	}

	/**
	 * get th elevator direction (only if motor is on)
	 * @return string
	 */
	public String getElevDirection() {
		return elevatorDirection;
	}

	/**
	 * return the ID of the elevator
	 * @return int
	 */
	public int getID(){return ID;}


	/**
	 * return the status of the motor
	 * @return boolean
	 */
	public boolean getIsMotorOn(){return isMotorOn;}

	/**
	 * returns status of door
	 * @return boolean
	 */
	public boolean getIsDoorOpen(){return isDoorOpen;}
}
