package input;

import elevatorSubsystem.ElevatorSubsystem;
import floorSubsystem.FloorSubsystem;
import scheduler.Scheduler;

public class LaunchElevator {

    public static void main(String[] args) {
    	
        Scheduler buffer = new Scheduler(4,2);
        Thread t1 = new Thread(new FloorSubsystem(4),"Floor Thread");
        Thread t2 = new Thread(new ElevatorSubsystem(buffer), "Elevator Thread");
        t1.start();
        t2.start();

    }
}
