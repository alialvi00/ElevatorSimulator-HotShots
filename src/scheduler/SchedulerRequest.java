package scheduler;

public class SchedulerRequest {

	private int currentFloor;
	private int destinationFloor;
	private String arrivalTime;
	private int direction;
	
	public SchedulerRequest() {}
	
	
	public void setCurrentFloor(int currentFloor) {
		this.currentFloor = currentFloor;
	}
	
	public void setDestinationFloor(int destinationFloor) {
		this.destinationFloor = destinationFloor;
	}
	
	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public int getCurrentFloor() {
		return currentFloor;
	}
	
	public int getDestinationFloor() {
		return destinationFloor;
	}
	
	public String getArrivalTime() {
		return arrivalTime;
	}
	
	public int getDirection() {
		return direction;
	}
	
	public boolean isEmpty() {
		return arrivalTime.isEmpty() || destinationFloor == 0 || direction == 0 || currentFloor == 0;	
	}
}
