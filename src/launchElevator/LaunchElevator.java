package launchElevator;

import elevatorSubsystem.ElevatorSubsystem;
import floorSubsystem.FloorSubsystem;
import scheduler.Scheduler;

public class LaunchElevator {

    public static void main(String[] args) {
    	
    	Thread t2 = new Thread(new ElevatorSubsystem(), "Elevator Thread");
    	Thread s = new Thread(new Scheduler(4,1));
        Thread t1 = new Thread(new FloorSubsystem(4,1),"Floor Thread");
        
        t2.start();
        s.start();
        t1.start();
        //Thread t2 = new Thread(new ElevatorSubsystem(buffer), "Elevator Thread");
    }
}
