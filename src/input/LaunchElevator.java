package input;

import elevatorSubsystem.ElevatorSubsystem;
import floorSubsystem.FloorSubsystem;
import scheduler.Scheduler;

public class LaunchElevator {

    public static void main(String[] args) {
    	
        Scheduler buffer = new Scheduler();
        InputBuffer input = new InputBuffer();
        Thread reader = new Thread(new Reader(input), "Reader Thread");
        Thread t1 = new Thread(new FloorSubsystem(buffer, input, 4),"Floor Thread");
        Thread t2 = new Thread(new ElevatorSubsystem(buffer), "Elevator Thread");
        reader.start();
        try {
            reader.join();
        } catch (Exception e){e.printStackTrace();}
        t1.start();
        t2.start();

    }
}
