package floorSubsystem;

public class UpdateFloors implements Runnable{
	
	FloorSubsystem floorSubsystem;
	
	public UpdateFloors(FloorSubsystem floorSubsystem) {
		this.floorSubsystem = floorSubsystem;
	}
	
	
	@Override
	public void run() {
		
		while(true) {
			floorSubsystem.updateFloors();
		}
	}
}
