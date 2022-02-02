import elevatorSubsystem.ElevatorSubsystem;
import floorSubsystem.FloorSubsystem;
import scheduler.Scheduler;

public class LaunchElevator {

	public static void main(String[] args) {
		
		Scheduler scheduler = new Scheduler();
		Thread ground_floor = new Thread(new FloorSubsystem(scheduler,1), "Ground Floor");
		Thread elevator = new Thread(new ElevatorSubsystem(scheduler), "Elevator");
		
		ground_floor.start();
		elevator.start();
		
		//We will make each thread wait for the other one to complete executing by calling join on each thread in an order
		try {
			ground_floor.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		try {
			elevator.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		
	}

}
