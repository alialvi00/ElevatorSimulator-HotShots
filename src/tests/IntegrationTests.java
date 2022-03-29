package tests;

import elevatorSubsystem.Elevator;
import elevatorSubsystem.ElevatorSubsystem;
import floorSubsystem.FloorSubsystem;
import org.junit.Test;
import scheduler.Scheduler;

import static org.junit.Assert.assertTrue;

/**
 * This class will test the integration of the three subsystems and how they can handle faults.
 */
public class IntegrationTests
{

    /**
     * This test will check to see how our code will behave once our scheduler experiences a elevator fault.
     * @throws InterruptedException
     */
    @Test
    public void integrationTestOnFailure() throws InterruptedException {
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
        FloorSubsystem floorSubsystem = new FloorSubsystem(22, 2);
        Scheduler scheduler = new Scheduler(22);

        Thread es = new Thread(elevatorSubsystem);
        Thread fs = new Thread(floorSubsystem);
        Thread s = new Thread(scheduler);



        es.start();
        fs.start();
        s.start();

        es.join(10000);
        fs.join(10000);
        s.join(10000);

        System.out.println("\n \n \n ---TESTS---");
        //the two elevators that the elevator subsystem will map depending on num of elevators.
        Elevator elevator1 = elevatorSubsystem.getElevatorMapping().get(1);
        Elevator elevator2 = elevatorSubsystem.getElevatorMapping().get(2);

        if(elevator1.isFailure() && elevator2.isFailure()) {
            System.out.println("ELEVATOR 1 AND 2: HAD A HARD FAILURE");
            System.out.println("ELEVATOR LEFT: NONE AVAILABLE");
            assertTrue(scheduler.getBestElevID() != 1);
            assertTrue(scheduler.getBestElevID() != 2);
        }
        else if(elevator1.isFailure()){
            System.out.println("ELEVATOR 1: HAD A HARD FAILURE");
            System.out.println("ELEVATOR LEFT: ELEVATOR 2" );
            assertTrue(scheduler.getBestElevID() != 1); //IF ELEV 1 HAS A HARD FAILURE, WE EXPECT ELEVATOR 2 TO BE THE BEST AVAILABLE ELEVATOR
        }
        else if(elevator2.isFailure()){
            System.out.println("ELEVATOR 2: HAD A HARD FAILURE");
            System.out.println("ELEVATOR LEFT: ELEVATOR 1" );
            assertTrue(scheduler.getBestElevID() !=2); //IF ELEV 1 HAS A HARD FAILURE, WE EXPECT ELEVATOR 2 TO BE THE BEST AVAILABLE ELEVATOR
        }



    }}