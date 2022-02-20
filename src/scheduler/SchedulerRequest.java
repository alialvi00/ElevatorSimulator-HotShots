package scheduler;

/**
 * This class represents the request packet sent by the subsystems to transfer data
 * @author Ali Alvi
 * @version 19/02/2022
 *
 */
public class SchedulerRequest {

	private int currentFloor; //field storing currentFloor
	private int destinationFloor; //field storing destinationFloor
	private String arrivalTime; //field storing arrival time in String
	private int direction; //field storing direction in integer i.e. 1 is up 0 is down
	private String subsystem; //field storing what subsystem the request is sent from
	
	/**
	 * Constructor that instantiates the scheduler request class
	 */
	public SchedulerRequest() {}
	
	
	/**
	 * This method sets the current floor
	 * @param currentFloor is an integer storing current floor
	 */
	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}
	
	/**
	 * This method sets the destination floor
	 * @param destinationFloor is an integer storing destination where elevator goes
	 */
	public void setDestinationFloor(int destinationFloor) {
		this.destinationFloor = destinationFloor;
	}
	
	/**
	 * This method sets the arrival time
	 * @param arrivalTime is a string storing arrival time
	 */
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	
	/**
	 * This method sets the direction of the elevator
	 * @param direction is an integer that shows direction
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	/**
	 * This method sets the subsystem that request is coming from
	 * @param subsystem is the string containing the origin subsystem
	 */
	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}
	
	/**
	 * This method gets the subsystem that request is coming from
	 * @return String containing which subsystem the request originates from
	 */
	public String getSubsystem() {
		return subsystem;
	}
	
	/**
	 * This method gets the currentFloor that request holds
	 * @return integer containing current floor
	 */
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	/**
	 * This method gets destination floor that request holds
	 * @return integer containing destination floor
	 */
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	/**
	 * This method gets arrival time that request holds
	 * @return String containing arrival time
	 */
	public String getArrivalTime() {
		return arrivalTime;
	}
	
	/**
	 * This method gets direction where elevator is moving
	 * @return integer containing direction
	 */
	public int getDirection() {
		return direction;
	}
	
	/**
	 * This method checks if request is incomplete
	 * @return Boolean value that tells if request is empty 
	 */
	public boolean isEmpty() {
		return arrivalTime.isEmpty() || destinationFloor == 0 || direction == 0 || currentFloor == 0;	
	}
}
