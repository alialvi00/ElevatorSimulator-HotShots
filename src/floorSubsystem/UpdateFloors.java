package floorSubsystem;

public class UpdateFloors implements Runnable{
	
	/**Local variable to store the floor subsystem. */
	FloorSubsystem floorSubsystem;
	
	/**
	 * Constructor for the UpdateFloors Class
	 * @param floorSubsystem - The floor subsystem. 
	 */
	public UpdateFloors(FloorSubsystem floorSubsystem) {
		this.floorSubsystem = floorSubsystem;
	}
	
	
	@Override
	public void run() {
		
		while(true) {
			//calls the update floors helper function. 
			floorSubsystem.updateFloors();
		}
	}
}
