package tests;

import elevatorSubsystem.Elevator;
import elevatorSubsystem.ElevatorSubsystem;
import floorSubsystem.FloorSubsystem;
import org.junit.Test;
import scheduler.Scheduler;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * This class will test the integration of the three subsystems and how they can handle faults.
 */
public class IntegrationTests
{

    /**
     * This test will check to see how our code will behave once our scheduler experiences a elevator fault.
     * FYI this is a full run through test so expect to let it run for around 45 seconds - 1 min. This is to allow a random decomission to occur
     *
     * STEPS: SINCE THIS IS AN INTEGRATION TEST YOU MUST SELECT OPTIONS THAT SHOW UP ON THE GUI SCREEN ONCE THE TEST IS RUN
     * PLEASE SELECT DEFAULT VALUES FOR WHENEVER YOU ARE PROMPTED FOR AN INPUT FOR ELEVATORS/FLOORS
     * @throws InterruptedException
     */
    @Test
    public void integrationTest() throws InterruptedException {
        ElevatorSubsystem elevatorSubsystem = new ElevatorSubsystem();
        FloorSubsystem floorSubsystem = new FloorSubsystem(22, 4);
        Scheduler scheduler = new Scheduler(22, 4);

        Thread es = new Thread(elevatorSubsystem);
        Thread fs = new Thread(floorSubsystem);
        Thread s = new Thread(scheduler);

        es.start();
        fs.start();
        s.start();

        es.join(25000);
        fs.join(25000);
        s.join(25000);

        System.out.println("\n \n \n ---TESTS---");
        List<Elevator> listOfElevators = new ArrayList<>();

        for(int i = 0; i < elevatorSubsystem.getElevatorMapping().size(); i++){
            listOfElevators.add(elevatorSubsystem.getElevatorMapping().get(i+1));
        }

        for(int i = 0; i < listOfElevators.size(); i++){
            if(listOfElevators.get(i).isFailure()){
                System.out.println("ELEVATOR: " + (i+1) + " HAD A HARD FAILURE");
                assertTrue(scheduler.getBestElevID()!= i+1);
            }
        }



    }}