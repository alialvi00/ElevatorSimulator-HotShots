package input;

import elevatorSubsystem.ElevatorSubsystem;
import floorSubsystem.FloorSubsystem;
import scheduler.Scheduler;

public class LaunchElevator {

    public static void main(String[] args) {
    	
        Scheduler buffer = new Scheduler();
        Thread reader = new Thread(new Reader(), "Reader Thread");
        Thread t1 = new Thread(new FloorSubsystem(buffer),"Floor Thread");
        Thread t2 = new Thread(new ElevatorSubsystem(buffer), "Elevator Thread");
        reader.start();
        try {
            reader.join();
        } catch (Exception e){e.printStackTrace();}
        t1.start();
        t2.start();

    }
}
